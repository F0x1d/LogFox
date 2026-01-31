package com.f0x1d.logfox.feature.logging.presentation.service

import android.content.Context
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.ProcessLogLineCrashesUseCase
import com.f0x1d.logfox.feature.logging.api.domain.AddLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.ClearLogsUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLastLogUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsSnapshotUseCase
import com.f0x1d.logfox.feature.logging.api.domain.StartLoggingUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateLogsUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.domain.terminal.ShouldFallbackToDefaultTerminalUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.NotifyLoggingStoppedUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.ProcessLogLineRecordingUseCase
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.domain.ExitTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.GetDefaultTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.GetSelectedTerminalUseCase
import com.f0x1d.logfox.feature.terminals.exception.TerminalNotSupportedException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class LoggingServiceEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val startLoggingUseCase: StartLoggingUseCase,
    private val addLogLineUseCase: AddLogLineUseCase,
    private val clearLogsUseCase: ClearLogsUseCase,
    private val getLogsSnapshotUseCase: GetLogsSnapshotUseCase,
    private val getLastLogUseCase: GetLastLogUseCase,
    private val updateLogsUseCase: UpdateLogsUseCase,
    private val processLogLineCrashesUseCase: ProcessLogLineCrashesUseCase,
    private val processLogLineRecordingUseCase: ProcessLogLineRecordingUseCase,
    private val notifyLoggingStoppedUseCase: NotifyLoggingStoppedUseCase,
    private val getSelectedTerminalUseCase: GetSelectedTerminalUseCase,
    private val getDefaultTerminalUseCase: GetDefaultTerminalUseCase,
    private val exitTerminalUseCase: ExitTerminalUseCase,
    private val getLogsUpdateIntervalUseCase: GetLogsUpdateIntervalUseCase,
    private val shouldFallbackToDefaultTerminalUseCase: ShouldFallbackToDefaultTerminalUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : EffectHandler<LoggingServiceSideEffect, LoggingServiceCommand> {

    private val effectScope = CoroutineScope(defaultDispatcher + SupervisorJob())

    private var logCollectionJob: Job? = null
    private var logUpdatesJob: Job? = null

    override suspend fun handle(
        effect: LoggingServiceSideEffect,
        onCommand: suspend (LoggingServiceCommand) -> Unit,
    ) {
        when (effect) {
            is LoggingServiceSideEffect.SelectTerminal -> {
                val terminal = getSelectedTerminalUseCase()
                Timber.d("Selected terminal: $terminal")
                onCommand(LoggingServiceCommand.TerminalSelected(terminal))
            }

            is LoggingServiceSideEffect.StartLogCollection -> {
                Timber.d("Starting log collection with terminal: ${effect.terminal}")
                val lastLog = getLastLogUseCase()
                val startingId = lastLog?.id?.plus(1) ?: 0L

                logCollectionJob?.cancel()
                logCollectionJob = effectScope.launch {
                    startLoggingUseCase(
                        terminal = effect.terminal,
                        startingId = startingId,
                        lastLogTime = lastLog?.dateAndTime,
                    ).catch { throwable ->
                        Timber.e(throwable, "Logging flow error")
                        if (throwable !is CancellationException) {
                            onCommand(
                                LoggingServiceCommand.LoggingError(
                                    error = throwable,
                                    terminal = effect.terminal,
                                )
                            )
                        }
                    }.collect { logLine ->
                        withContext(defaultDispatcher) {
                            addLogLineUseCase(logLine)
                            processLogLineCrashesUseCase(logLine)
                            processLogLineRecordingUseCase(logLine)
                        }
                    }

                    // Flow completed normally
                    onCommand(LoggingServiceCommand.LoggingFlowCompleted)
                }
            }

            is LoggingServiceSideEffect.StopLogCollection -> {
                Timber.d("Stopping log collection")
                logCollectionJob?.cancel()
                logCollectionJob = null
            }

            is LoggingServiceSideEffect.ClearLogs -> {
                Timber.d("Clearing logs")
                clearLogsUseCase()
            }

            is LoggingServiceSideEffect.ScheduleLogUpdates -> {
                Timber.d("Scheduling log updates")
                logUpdatesJob?.cancel()
                logUpdatesJob = effectScope.launch {
                    while (true) {
                        delay(getLogsUpdateIntervalUseCase())
                        val logs = getLogsSnapshotUseCase()
                        Timber.d("Sending ${logs.size} logs to store")
                        updateLogsUseCase(logs)
                    }
                }
            }

            is LoggingServiceSideEffect.CancelLogUpdates -> {
                Timber.d("Cancelling log updates")
                logUpdatesJob?.cancel()
                logUpdatesJob = null
            }

            is LoggingServiceSideEffect.NotifyLoggingStopped -> {
                Timber.d("Notifying logging stopped")
                notifyLoggingStoppedUseCase()
            }

            is LoggingServiceSideEffect.ExitTerminal -> {
                Timber.d("Exiting terminal: ${effect.terminal}")
                exitTerminalUseCase(effect.terminal)
            }

            is LoggingServiceSideEffect.HandleLoggingError -> {
                val error = effect.error
                val terminal = effect.terminal

                when {
                    error is TerminalNotSupportedException -> {
                        if (shouldFallbackToDefaultTerminalUseCase()) {
                            val message = context.getString(Strings.terminal_unavailable_falling_back)
                            onCommand(LoggingServiceCommand.ShowToast(message))
                            onCommand(
                                LoggingServiceCommand.TerminalFallback(
                                    newTerminal = getDefaultTerminalUseCase(),
                                )
                            )
                        } else {
                            // Wait and retry with same terminal
                            delay(RETRY_DELAY_MS)
                            onCommand(
                                LoggingServiceCommand.TerminalSelected(terminal)
                            )
                        }
                    }
                    error is CancellationException -> {
                        // Ignore cancellation
                    }
                    else -> {
                        val message = context.getString(
                            Strings.error,
                            error.localizedMessage ?: error.message ?: "Unknown error",
                        )
                        onCommand(LoggingServiceCommand.ShowToast(message))
                        error.printStackTrace()
                        // Wait and retry
                        delay(RETRY_DELAY_MS)
                        onCommand(
                            LoggingServiceCommand.TerminalSelected(terminal)
                        )
                    }
                }
            }

            // UI side effects - handled by Service, ignored here
            is LoggingServiceSideEffect.ShowToast -> Unit
            is LoggingServiceSideEffect.PerformKillService -> Unit
        }
    }

    override fun close() {
        logCollectionJob?.cancel()
        logCollectionJob = null
        logUpdatesJob?.cancel()
        logUpdatesJob = null
    }

    companion object {
        private const val RETRY_DELAY_MS = 10_000L
    }
}
