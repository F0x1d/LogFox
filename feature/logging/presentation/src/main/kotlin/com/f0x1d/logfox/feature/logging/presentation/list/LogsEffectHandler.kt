package com.f0x1d.logfox.feature.logging.presentation.list

import android.content.Context
import com.f0x1d.logfox.core.coroutines.combine
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
import com.f0x1d.logfox.feature.logging.presentation.list.domain.ClearSelectionUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.GetSelectedIdsUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.ObserveExpandedOverridesUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.ObserveSelectedIdsUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.SelectAllItemsUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.SetItemSelectedUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.ToggleItemExpandedUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.domain.ToggleItemSelectedUseCase
import com.f0x1d.logfox.feature.logging.presentation.list.model.toPresentationModel
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedUseCase
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
    private val observeSelectedIdsUseCase: ObserveSelectedIdsUseCase,
    private val getSelectedIdsUseCase: GetSelectedIdsUseCase,
    private val toggleItemSelectedUseCase: ToggleItemSelectedUseCase,
    private val setItemSelectedUseCase: SetItemSelectedUseCase,
    private val selectAllItemsUseCase: SelectAllItemsUseCase,
    private val clearSelectionUseCase: ClearSelectionUseCase,
    private val observeExpandedOverridesUseCase: ObserveExpandedOverridesUseCase,
    private val toggleItemExpandedUseCase: ToggleItemExpandedUseCase,
    private val getLogsExpandedUseCase: GetLogsExpandedUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<LogsSideEffect, LogsCommand> {

    override suspend fun handle(effect: LogsSideEffect, onCommand: suspend (LogsCommand) -> Unit) {
        when (effect) {
            is LogsSideEffect.LoadLogs -> {
                val formattedLogsFlow = combine(
                    getLogsFlowUseCase(),
                    getAllEnabledFiltersFlowUseCase(),
                    getQueryFlowUseCase(),
                    getCaseSensitiveFlowUseCase(),
                    getPausedFlowUseCase(),
                    getShowLogValuesFlowUseCase(),
                ) { logs, filters, query, caseSensitive, paused, showLogValues ->
                    LogsData(
                        logs = logs,
                        filters = filters,
                        query = query,
                        caseSensitive = caseSensitive,
                        paused = paused,
                        showLogValues = showLogValues,
                    )
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
                    FormattedLogsData(
                        formattedLogs = data.logs.map { line ->
                            FormattedLogLine(
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

                combine(
                    formattedLogsFlow,
                    observeSelectedIdsUseCase(),
                    observeExpandedOverridesUseCase(),
                    getLogsTextSizeFlowUseCase(),
                    getLogsExpandedFlowUseCase(),
                ) { logsData, selectedIds, expandedOverrides, textSize, logsExpanded ->
                    LogsCommand.LogsLoaded(
                        logs = logsData.formattedLogs.map { formatted ->
                            formatted.logLine.toPresentationModel(
                                displayText = formatted.displayText,
                                expanded = expandedOverrides.getOrElse(formatted.logLine.id) { logsExpanded },
                                selected = formatted.logLine.id in selectedIds,
                                textSize = textSize.toFloat(),
                            )
                        },
                        query = logsData.query,
                        filters = logsData.filters,
                        selecting = selectedIds.isNotEmpty(),
                        selectedCount = selectedIds.size,
                    )
                }.flowOn(defaultDispatcher)
                    .collect { command ->
                        onCommand(command)
                    }
            }

            is LogsSideEffect.ObservePreferences -> {
                getResumeLoggingWithBottomTouchFlowUseCase()
                    .collect { value ->
                        onCommand(LogsCommand.PreferencesUpdated(resumeLoggingWithBottomTouch = value))
                    }
            }

            is LogsSideEffect.ObserveSelection -> {
                observeSelectedIdsUseCase().collect { selectedIds ->
                    val lines = getLogLinesByIdsUseCase(selectedIds)
                    updateSelectedLogLinesUseCase(selectedLines = lines)
                }
            }

            is LogsSideEffect.ObservePausedState -> {
                getPausedFlowUseCase().collect { paused ->
                    onCommand(LogsCommand.PausedStateUpdated(paused))
                }
            }

            is LogsSideEffect.UpdatePaused -> {
                updatePausedUseCase(effect.paused)
            }

            is LogsSideEffect.ToggleItemSelection -> {
                toggleItemSelectedUseCase(effect.logLineId)
            }

            is LogsSideEffect.SetItemSelected -> {
                setItemSelectedUseCase(effect.logLineId, effect.selected)
            }

            is LogsSideEffect.SelectAllItems -> {
                selectAllItemsUseCase(effect.allIds)
            }

            is LogsSideEffect.ClearSelection -> {
                clearSelectionUseCase()
            }

            is LogsSideEffect.ToggleItemExpanded -> {
                val defaultExpanded = getLogsExpandedUseCase()
                toggleItemExpandedUseCase(effect.logLineId, defaultExpanded)
            }

            is LogsSideEffect.CreateRecordingFromLines -> {
                withContext(defaultDispatcher) {
                    runCatching {
                        val lineIds = getSelectedIdsUseCase()
                        val lines = getLogLinesByIdsUseCase(lineIds)
                            .sortedBy { it.dateAndTime }
                        createRecordingFromLinesUseCase(lines = lines)
                    }
                }
            }

            is LogsSideEffect.ExportLogsTo -> {
                withContext(ioDispatcher) {
                    runCatching {
                        val lineIds = getSelectedIdsUseCase()
                        val lines = getLogLinesByIdsUseCase(lineIds)
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
                val lineIds = getSelectedIdsUseCase()
                val lines = getLogLinesByIdsUseCase(lineIds)
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

    private data class FormattedLogsData(
        val formattedLogs: List<FormattedLogLine>,
        val query: String?,
        val filters: List<UserFilter>,
    )

    private data class FormattedLogLine(
        val logLine: LogLine,
        val displayText: CharSequence,
    )
}
