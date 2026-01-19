package com.f0x1d.logfox.feature.logging.service.presentation

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.f0x1d.logfox.core.context.EXIT_APP_INTENT_ID
import com.f0x1d.logfox.core.context.OPEN_APP_INTENT_ID
import com.f0x1d.logfox.core.context.activityManager
import com.f0x1d.logfox.core.context.makeServicePendingIntent
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.presentation.Icons
import com.f0x1d.logfox.feature.crashes.api.domain.ProcessLogLineCrashesUseCase
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.model.suits
import com.f0x1d.logfox.feature.logging.api.domain.StartLoggingUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateLogsUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.notifications.api.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.feature.preferences.domain.GetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.domain.ShouldFallbackToDefaultTerminalUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.NotifyLoggingStoppedUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.ProcessLogLineRecordingUseCase
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.domain.ExitTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.GetDefaultTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.GetSelectedTerminalUseCase
import com.f0x1d.logfox.feature.terminals.exception.TerminalNotSupportedException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.LinkedList
import javax.inject.Inject

@AndroidEntryPoint
class LoggingService : LifecycleService() {

    companion object {
        const val ACTION_RESTART_LOGGING = "logfox.RESTART_LOGGING"
        const val ACTION_CLEAR_LOGS = "logfox.CLEAR_LOGS"

        const val ACTION_KILL_SERVICE = "logfox.KILL_SERVICE"
    }

    private val binder = LocalBinder()

    @Inject
    lateinit var startLoggingUseCase: StartLoggingUseCase

    @Inject
    lateinit var updateLogsUseCase: UpdateLogsUseCase

    @Inject
    lateinit var processLogLineCrashesUseCase: ProcessLogLineCrashesUseCase

    @Inject
    lateinit var processLogLineRecordingUseCase: ProcessLogLineRecordingUseCase

    @Inject
    lateinit var notifyLoggingStoppedUseCase: NotifyLoggingStoppedUseCase

    @Inject
    lateinit var getAllEnabledFiltersFlowUseCase: GetAllEnabledFiltersFlowUseCase

    @Inject
    lateinit var getSelectedTerminalUseCase: GetSelectedTerminalUseCase

    @Inject
    lateinit var getDefaultTerminalUseCase: GetDefaultTerminalUseCase

    @Inject
    lateinit var exitTerminalUseCase: ExitTerminalUseCase

    @Inject
    lateinit var getLogsUpdateIntervalUseCase: GetLogsUpdateIntervalUseCase

    @Inject
    lateinit var getLogsDisplayLimitUseCase: GetLogsDisplayLimitUseCase

    @Inject
    lateinit var shouldFallbackToDefaultTerminalUseCase: ShouldFallbackToDefaultTerminalUseCase

    @Inject
    lateinit var mainActivityPendingIntentProvider: MainActivityPendingIntentProvider

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    private val logs = LinkedList<LogLine>()
    private val logsMutex = Mutex()
    private var loggingJob: Job? = null

    private lateinit var filtersState: StateFlow<List<UserFilter>>

    override fun onCreate() {
        super.onCreate()

        startForeground(-1, notification())
        startLogging()

        filtersState = getAllEnabledFiltersFlowUseCase()
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Timber.d("got command ${intent?.action}")
        when (intent?.action) {
            ACTION_RESTART_LOGGING -> restartLogging()
            ACTION_CLEAR_LOGS -> clearLogs()
            ACTION_KILL_SERVICE -> killApp()
        }

        return START_NOT_STICKY
    }

    private fun startLogging() {
        Timber.d("startLogging")
        if (loggingJob?.isActive == true) return

        var loggingTerminal = getSelectedTerminalUseCase()

        Timber.d("selected terminal $loggingTerminal")

        loggingJob = lifecycleScope.launch {
            try {
                launch {
                    while (true) {
                        delay(getLogsUpdateIntervalUseCase())

                        logsMutex.withLock {
                            Timber.d("sending update logs to store")
                            updateLogsUseCase(logs)
                        }
                    }
                }

                while (true) {
                    Timber.d("in loop starting")

                    startLoggingUseCase(
                        terminal = loggingTerminal,
                        startingId = logs.lastOrNull()?.id ?: 0,
                    ).catch { throwable ->
                        Timber.e("logging flow threw smth ${throwable.localizedMessage}")

                        if (throwable is TerminalNotSupportedException) {
                            if (shouldFallbackToDefaultTerminalUseCase()) {
                                toast(Strings.terminal_unavailable_falling_back)

                                exitTerminalUseCase(loggingTerminal)
                                loggingTerminal = getDefaultTerminalUseCase()
                            } else {
                                delay(10000) // waiting for 10sec before new attempt
                            }
                        } else if (throwable !is CancellationException) {
                            toast(getString(Strings.error, throwable.localizedMessage))
                            throwable.printStackTrace()

                            delay(10000) // waiting for 10sec before new attempt
                        }
                    }.collect { logLine ->
                        withContext(defaultDispatcher) {
                            logsMutex.withLock {
                                logs.add(logLine)

                                while (logs.size > getLogsDisplayLimitUseCase()) {
                                    logs.removeFirst()
                                }
                            }

                            processLogLineCrashesUseCase(logLine)

                            if (logLine.suits(filtersState.value)) {
                                processLogLineRecordingUseCase(logLine)
                            }
                        }
                    }
                }
            } finally {
                Timber.d("finally block")
                withContext(NonCancellable) {
                    notifyLoggingStoppedUseCase()
                    clearLogs().join()

                    exitTerminalUseCase(loggingTerminal)
                }
            }
        }
    }

    private fun restartLogging() = lifecycleScope.launch {
        Timber.d("restaring logs")

        loggingJob?.cancelAndJoin()
        Timber.d("cancelled loggingJob")

        startLogging()
    }

    private fun clearLogs() = lifecycleScope.launch {
        Timber.d("clearing logs")
        logsMutex.withLock {
            logs.clear()
        }

        updateLogsUseCase(emptyList())
    }

    private fun notification() = NotificationCompat.Builder(this, LOGGING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(Strings.logging))
        .setSmallIcon(Icons.ic_logfox)
        .setOngoing(true)
        .setContentIntent(mainActivityPendingIntentProvider.provide(OPEN_APP_INTENT_ID))
        .addAction(
            Icons.ic_clear,
            getString(Strings.exit),
            makeServicePendingIntent<LoggingService>(EXIT_APP_INTENT_ID) {
                action = ACTION_KILL_SERVICE
            },
        )
        .build()

    private fun killApp() = lifecycleScope.launch {
        Timber.d("killing app")

        loggingJob?.cancelAndJoin()
        Timber.d("cancelled loggingJob and now can stop app")

        activityManager.appTasks.forEach {
            it.finishAndRemoveTask()
        }

        ServiceCompat.stopForeground(this@LoggingService, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    inner class LocalBinder : Binder() {
        val service: LoggingService get() = this@LoggingService
    }
}
