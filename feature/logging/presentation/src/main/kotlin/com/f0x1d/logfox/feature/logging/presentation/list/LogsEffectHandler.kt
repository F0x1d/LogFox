package com.f0x1d.logfox.feature.logging.presentation.list

import android.content.Context
import android.net.Uri
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetPausedFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.ReadLogFileUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdatePausedUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.presentation.di.FileUri
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LogsEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    @FileUri private val fileUri: Uri?,
    private val getLogsFlowUseCase: GetLogsFlowUseCase,
    private val getQueryFlowUseCase: GetQueryFlowUseCase,
    private val getAllEnabledFiltersFlowUseCase: GetAllEnabledFiltersFlowUseCase,
    private val updateSelectedLogLinesUseCase: UpdateSelectedLogLinesUseCase,
    private val createRecordingFromLinesUseCase: CreateRecordingFromLinesUseCase,
    private val getShowLogValuesFlowUseCase: GetShowLogValuesFlowUseCase,
    private val getLogsDisplayLimitUseCase: GetLogsDisplayLimitUseCase,
    private val getResumeLoggingWithBottomTouchFlowUseCase: GetResumeLoggingWithBottomTouchFlowUseCase,
    private val getLogsTextSizeFlowUseCase: GetLogsTextSizeFlowUseCase,
    private val getLogsExpandedFlowUseCase: GetLogsExpandedFlowUseCase,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    private val getPausedFlowUseCase: GetPausedFlowUseCase,
    private val updatePausedUseCase: UpdatePausedUseCase,
    private val readLogFileUseCase: ReadLogFileUseCase,
    private val dateTimeFormatter: DateTimeFormatter,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<LogsSideEffect, LogsCommand> {

    private val viewingFile = fileUri != null

    override suspend fun handle(effect: LogsSideEffect, onCommand: suspend (LogsCommand) -> Unit) {
        when (effect) {
            is LogsSideEffect.LoadLogs -> {
                combine(
                    fileUri?.let {
                        readLogFileUseCase(
                            uri = it,
                            logsDisplayLimit = getLogsDisplayLimitUseCase(),
                        )
                    } ?: getLogsFlowUseCase(),
                    getAllEnabledFiltersFlowUseCase(),
                    getQueryFlowUseCase(),
                    if (!viewingFile) getPausedFlowUseCase() else flowOf(false),
                ) { logs, filters, query, paused ->
                    LogsData(
                        logs = logs,
                        filters = filters,
                        query = query,
                        paused = paused,
                    )
                }.scan(LogsData()) { accumulator, data ->
                    when {
                        !data.paused || data.logs.isEmpty() -> {
                            data
                        }

                        data.query != accumulator.query ||
                            data.filters != accumulator.filters -> {
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
                        ),
                    )
                }.flowOn(defaultDispatcher)
                    .collect { data ->
                        onCommand(
                            LogsCommand.LogsLoaded(
                                logs = data.logs,
                                query = data.query,
                                filters = data.filters,
                            ),
                        )
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
                updateSelectedLogLinesUseCase(selectedLines = effect.selectedLines)
            }

            is LogsSideEffect.CreateRecordingFromLines -> {
                withContext(defaultDispatcher) {
                    runCatching {
                        createRecordingFromLinesUseCase(lines = effect.lines)
                    }
                }
            }

            is LogsSideEffect.ExportLogsTo -> {
                withContext(ioDispatcher) {
                    runCatching {
                        val content = effect.lines.joinToString("\n") { line ->
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
                val formattedText = formatLogLineUseCase(
                    logLine = effect.logLine,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
                onCommand(LogsCommand.CopyFormattedText(formattedText))
            }

            is LogsSideEffect.FormatAndCopyLogs -> {
                val formattedText = effect.logLines.joinToString("\n") { line ->
                    formatLogLineUseCase(
                        logLine = line,
                        formatDate = dateTimeFormatter::formatDate,
                        formatTime = dateTimeFormatter::formatTime,
                    )
                }
                onCommand(LogsCommand.CopyFormattedText(formattedText))
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
        val paused: Boolean = false,
        val passing: Boolean = true,
    )
}
