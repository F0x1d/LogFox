package com.f0x1d.logfox.feature.logging.presentation.list

import android.content.Context
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogLinesByIdsUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.api.presentation.LoggingServiceDelegate
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LogsEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getLogsFlowUseCase: GetLogsFlowUseCase,
    private val getQueryFlowUseCase: GetQueryFlowUseCase,
    private val getCaseSensitiveFlowUseCase: GetCaseSensitiveFlowUseCase,
    private val getAllEnabledFiltersFlowUseCase: GetAllEnabledFiltersFlowUseCase,
    private val updateSelectedLogLinesUseCase: UpdateSelectedLogLinesUseCase,
    private val createRecordingFromLinesUseCase: CreateRecordingFromLinesUseCase,
    private val getLogLinesByIdsUseCase: GetLogLinesByIdsUseCase,
    private val getShowLogValuesFlowUseCase: GetShowLogValuesFlowUseCase,
    private val getResumeLoggingWithBottomTouchFlowUseCase: GetResumeLoggingWithBottomTouchFlowUseCase,
    private val getLogsTextSizeFlowUseCase: GetLogsTextSizeFlowUseCase,
    private val getLogsExpandedFlowUseCase: GetLogsExpandedFlowUseCase,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    private val loggingServiceDelegate: LoggingServiceDelegate,
    private val dateTimeFormatter: DateTimeFormatter,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<LogsSideEffect, LogsCommand> {

    override suspend fun handle(effect: LogsSideEffect, onCommand: suspend (LogsCommand) -> Unit) {
        when (effect) {
            is LogsSideEffect.LoadLogs -> {
                combine(
                    getLogsFlowUseCase(),
                    getAllEnabledFiltersFlowUseCase(),
                    getQueryFlowUseCase(),
                    getCaseSensitiveFlowUseCase(),
                    getShowLogValuesFlowUseCase(),
                ) { logs, filters, query, caseSensitive, showLogValues ->
                    LogsCommand.LogsLoaded(
                        logs = logs,
                        query = query,
                        caseSensitive = caseSensitive,
                        filters = filters,
                        showLogValues = showLogValues,
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is LogsSideEffect.ObservePreferences -> {
                combine(
                    getResumeLoggingWithBottomTouchFlowUseCase(),
                    getLogsTextSizeFlowUseCase(),
                    getLogsExpandedFlowUseCase(),
                ) { resumeLoggingWithBottomTouch, textSize, logsExpanded ->
                    LogsCommand.PreferencesUpdated(
                        resumeLoggingWithBottomTouch = resumeLoggingWithBottomTouch,
                        textSize = textSize,
                        logsExpanded = logsExpanded,
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is LogsSideEffect.SyncSelectedLines -> {
                val lines = getLogLinesByIdsUseCase(effect.selectedIds)
                updateSelectedLogLinesUseCase(selectedLines = lines)
            }

            is LogsSideEffect.CreateRecordingFromLines -> {
                withContext(defaultDispatcher) {
                    runCatching {
                        val lines = getLogLinesByIdsUseCase(effect.selectedIds)
                        createRecordingFromLinesUseCase(lines = lines)
                    }
                }
            }

            is LogsSideEffect.ExportLogsTo -> {
                withContext(ioDispatcher) {
                    runCatching {
                        val lines = getLogLinesByIdsUseCase(effect.selectedIds)
                        val content = lines.joinToString("\n") { line ->
                            formatLogLineUseCase(
                                logLine = line,
                                formatDate = dateTimeFormatter::formatDate,
                                formatTime = dateTimeFormatter::formatTime,
                            )
                        }
                        context.contentResolver.openOutputStream(effect.uri)?.use {
                            it.write(content.encodeToByteArray())
                        }
                    }
                }
            }

            is LogsSideEffect.FormatAndCopyLog -> {
                val logLine = getLogLinesByIdsUseCase(setOf(effect.logLineId))
                    .firstOrNull() ?: return@handle
                val formattedText = formatLogLineUseCase(
                    logLine = logLine,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
                onCommand(LogsCommand.CopyFormattedText(formattedText))
            }

            is LogsSideEffect.FormatAndCopyLogs -> {
                val lines = getLogLinesByIdsUseCase(effect.selectedIds)
                val formattedText = lines.joinToString("\n") { line ->
                    formatLogLineUseCase(
                        logLine = line,
                        formatDate = dateTimeFormatter::formatDate,
                        formatTime = dateTimeFormatter::formatTime,
                    )
                }
                onCommand(LogsCommand.CopyFormattedText(formattedText))
            }

            is LogsSideEffect.ClearLogs -> {
                loggingServiceDelegate.clearLogs()
            }

            is LogsSideEffect.RestartLogging -> {
                loggingServiceDelegate.restartLogging()
            }

            is LogsSideEffect.KillService -> {
                loggingServiceDelegate.killService()
            }

            // UI side effects - handled by Fragment
            else -> Unit
        }
    }
}
