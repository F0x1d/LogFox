package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.ExportLogsToUriUseCase
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.api.presentation.LoggingServiceDelegate
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsExpandedFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class LogsEffectHandler @Inject constructor(
    private val getLogsFlowUseCase: GetLogsFlowUseCase,
    private val getQueryFlowUseCase: GetQueryFlowUseCase,
    private val getCaseSensitiveFlowUseCase: GetCaseSensitiveFlowUseCase,
    private val getAllEnabledFiltersFlowUseCase: GetAllEnabledFiltersFlowUseCase,
    private val updateSelectedLogLinesUseCase: UpdateSelectedLogLinesUseCase,
    private val createRecordingFromLinesUseCase: CreateRecordingFromLinesUseCase,
    private val getShowLogValuesFlowUseCase: GetShowLogValuesFlowUseCase,
    private val getResumeLoggingWithBottomTouchFlowUseCase: GetResumeLoggingWithBottomTouchFlowUseCase,
    private val getLogsTextSizeFlowUseCase: GetLogsTextSizeFlowUseCase,
    private val getLogsExpandedFlowUseCase: GetLogsExpandedFlowUseCase,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    private val exportLogsToUriUseCase: ExportLogsToUriUseCase,
    private val loggingServiceDelegate: LoggingServiceDelegate,
    private val dateTimeFormatter: DateTimeFormatter,
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
                updateSelectedLogLinesUseCase(selectedLines = effect.lines)
            }

            is LogsSideEffect.CreateRecordingFromLines -> {
                createRecordingFromLinesUseCase(lines = effect.lines)
            }

            is LogsSideEffect.ExportLogsTo -> {
                exportLogsToUriUseCase(effect.lines, effect.uri)
            }

            is LogsSideEffect.FormatAndCopyLog -> {
                val formattedText = formatLogLineUseCase(
                    logLine = effect.logLine,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
                onCommand(LogsCommand.CopyFormattedText(formattedText))
            }

            is LogsSideEffect.FormatAndCopyLogs -> {
                val formattedText = effect.lines.joinToString("\n") { line ->
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
