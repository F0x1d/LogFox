package com.f0x1d.logfox.feature.logging.presentation.list

import android.content.Context
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogLinesByIdsUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetPausedFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdatePausedUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.logging.api.presentation.LoggingServiceDelegate
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
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
    private val getPausedFlowUseCase: GetPausedFlowUseCase,
    private val updatePausedUseCase: UpdatePausedUseCase,
    private val loggingServiceDelegate: LoggingServiceDelegate,
    private val dateTimeFormatter: DateTimeFormatter,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<LogsSideEffect, LogsCommand> {

    override suspend fun handle(effect: LogsSideEffect, onCommand: suspend (LogsCommand) -> Unit) {
        when (effect) {
            is LogsSideEffect.LoadLogs -> {
                combine(
                    combine(
                        getLogsFlowUseCase(),
                        getAllEnabledFiltersFlowUseCase(),
                        getQueryFlowUseCase(),
                        getCaseSensitiveFlowUseCase(),
                        getPausedFlowUseCase(),
                    ) { logs, filters, query, caseSensitive, paused ->
                        LogsData(
                            logs = logs,
                            filters = filters,
                            query = query,
                            caseSensitive = caseSensitive,
                            paused = paused,
                        )
                    },
                    getShowLogValuesFlowUseCase(),
                ) { data, showLogValues ->
                    data.copy(showLogValues = showLogValues)
                }.scan(LogsData()) { accumulator, data ->
                    when {
                        !data.paused || data.logs.isEmpty() -> {
                            data
                        }

                        data.query != accumulator.query ||
                            data.caseSensitive != accumulator.caseSensitive ||
                            data.filters != accumulator.filters ||
                            data.showLogValues != accumulator.showLogValues -> {
                            data.copy(
                                logs = accumulator.logs,
                            )
                        }

                        else -> {
                            data.copy(
                                logs = accumulator.logs,
                                passing = false,
                            )
                        }
                    }
                }.filter { data ->
                    data.passing
                }.mapNotNull { data ->
                    data.copy(
                        logs = data.logs.filterAndSearch(
                            filters = data.filters,
                            query = data.query,
                            caseSensitive = data.caseSensitive,
                        ),
                    )
                }.map { data ->
                    LogsCommand.LogsLoaded(
                        logs = data.logs.map { line ->
                            LogsCommand.LogsLoaded.FormattedLogLine(
                                logLine = line,
                                displayText = line.formatOriginal(
                                    values = data.showLogValues,
                                    formatDate = dateTimeFormatter::formatDate,
                                    formatTime = dateTimeFormatter::formatTime,
                                ),
                            )
                        },
                        query = data.query,
                        filters = data.filters,
                    )
                }.flowOn(defaultDispatcher)
                    .collect { command ->
                        onCommand(command)
                    }
            }

            is LogsSideEffect.ObservePreferences -> {
                combine(
                    getResumeLoggingWithBottomTouchFlowUseCase(),
                    getLogsTextSizeFlowUseCase(),
                    getLogsExpandedFlowUseCase(),
                    getShowLogValuesFlowUseCase(),
                ) { resumeLoggingWithBottomTouch, logsTextSize, logsExpanded, logsFormat ->
                    LogsCommand.PreferencesUpdated(
                        resumeLoggingWithBottomTouch = resumeLoggingWithBottomTouch,
                        logsTextSize = logsTextSize.toFloat(),
                        logsExpanded = logsExpanded,
                        logsFormat = logsFormat,
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is LogsSideEffect.PauseStateChanged -> {
                updatePausedUseCase(effect.paused)
            }

            is LogsSideEffect.UpdateSelectedLogLines -> {
                val lines = getLogLinesByIdsUseCase(effect.selectedIds)
                updateSelectedLogLinesUseCase(selectedLines = lines)
            }

            is LogsSideEffect.CreateRecordingFromLines -> {
                withContext(defaultDispatcher) {
                    runCatching {
                        val lines = getLogLinesByIdsUseCase(effect.lineIds)
                            .sortedBy { it.dateAndTime }
                        createRecordingFromLinesUseCase(lines = lines)
                    }
                }
            }

            is LogsSideEffect.ExportLogsTo -> {
                withContext(ioDispatcher) {
                    runCatching {
                        val lines = getLogLinesByIdsUseCase(effect.lineIds)
                            .sortedBy { it.dateAndTime }
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
                val lines = getLogLinesByIdsUseCase(effect.lineIds)
                    .sortedBy { it.dateAndTime }
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
            else -> {
                Unit
            }
        }
    }

    private data class LogsData(
        val logs: List<LogLine> = emptyList(),
        val filters: List<UserFilter> = emptyList(),
        val query: String? = null,
        val caseSensitive: Boolean = false,
        val paused: Boolean = false,
        val passing: Boolean = true,
        val showLogValues: ShowLogValues = ShowLogValues(
            date = true,
            time = true,
            uid = false,
            pid = true,
            tid = true,
            packageName = false,
            tag = true,
            content = true,
        ),
    )
}
