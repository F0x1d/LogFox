package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.presentation.list.model.toPresentationModel
import javax.inject.Inject

internal class LogsReducer @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : Reducer<LogsState, LogsCommand, LogsSideEffect> {

    override fun reduce(
        state: LogsState,
        command: LogsCommand,
    ): ReduceResult<LogsState, LogsSideEffect> = when (command) {
        is LogsCommand.LogsLoaded -> {
            val selectedIds = state.selectedItemIds
            val newLogs = command.logs.map { formatted ->
                formatted.logLine.toPresentationModel(
                    displayText = formatted.displayText,
                    selected = formatted.logLine.id in selectedIds,
                )
            }
            state.copy(
                logs = newLogs,
                query = command.query,
                filters = command.filters,
                logsChanged = true,
            ).noSideEffects()
        }

        is LogsCommand.PreferencesUpdated -> {
            state.copy(
                resumeLoggingWithBottomTouch = command.resumeLoggingWithBottomTouch,
                logsTextSize = command.logsTextSize,
                logsExpanded = command.logsExpanded,
                logsFormat = command.logsFormat,
                logsChanged = false,
            ).noSideEffects()
        }

        is LogsCommand.SelectLine -> {
            val itemId = command.logLineItem.logLineId
            val newSelectedIds = if (command.selected) {
                state.selectedItemIds + itemId
            } else {
                state.selectedItemIds - itemId
            }
            val newLogs = state.logs?.map { it.copy(selected = it.logLineId in newSelectedIds) }
            val newState = state.copy(
                logs = newLogs,
                selectedItemIds = newSelectedIds,
                logsChanged = true,
            )
            newState.withSideEffects(
                LogsSideEffect.UpdateSelectedLogLines(
                    selectedIds = newState.selectedItemIds,
                ),
            )
        }

        is LogsCommand.SelectAll -> {
            val allLogs = state.logs ?: emptyList()
            val allSelected = allLogs.isNotEmpty() && allLogs.all { it.selected }
            val newSelectedIds = if (allSelected) {
                emptySet()
            } else {
                allLogs.mapTo(mutableSetOf()) { it.logLineId }
            }
            val newLogs = allLogs.map { it.copy(selected = it.logLineId in newSelectedIds) }
            val newState = state.copy(
                logs = newLogs,
                selectedItemIds = newSelectedIds,
                logsChanged = true,
            )
            newState.withSideEffects(
                LogsSideEffect.UpdateSelectedLogLines(
                    selectedIds = newState.selectedItemIds,
                ),
            )
        }

        is LogsCommand.SelectedToRecording -> {
            state.withSideEffects(
                LogsSideEffect.CreateRecordingFromLines(
                    lineIds = state.selectedItemIds,
                ),
                LogsSideEffect.NavigateToRecordings,
            )
        }

        is LogsCommand.ExportSelectedClicked -> {
            val filename = "${dateTimeFormatter.formatForExport(System.currentTimeMillis())}.log"
            state.withSideEffects(
                LogsSideEffect.LaunchExportPicker(filename = filename),
            )
        }

        is LogsCommand.ExportSelectedTo -> {
            state.withSideEffects(
                LogsSideEffect.ExportLogsTo(
                    uri = command.uri,
                    lineIds = state.selectedItemIds,
                ),
            )
        }

        is LogsCommand.SwitchState -> {
            val newPaused = !state.paused
            state.copy(
                paused = newPaused,
                logsChanged = false,
            ).withSideEffects(LogsSideEffect.PauseStateChanged(newPaused))
        }

        is LogsCommand.Pause -> {
            if (state.paused) {
                state.noSideEffects()
            } else {
                state.copy(
                    paused = true,
                    logsChanged = false,
                ).withSideEffects(LogsSideEffect.PauseStateChanged(true))
            }
        }

        is LogsCommand.Resume -> {
            if (!state.paused) {
                state.noSideEffects()
            } else {
                state.copy(
                    paused = false,
                    logsChanged = false,
                ).withSideEffects(LogsSideEffect.PauseStateChanged(false))
            }
        }

        is LogsCommand.ClearSelection -> {
            val newLogs = state.logs?.map { it.copy(selected = false) }
            state.copy(
                logs = newLogs,
                selectedItemIds = emptySet(),
                logsChanged = true,
            ).withSideEffects(
                LogsSideEffect.UpdateSelectedLogLines(selectedIds = emptySet()),
            )
        }

        is LogsCommand.CopyLog -> {
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLog(command.logLineItem.logLineId),
            )
        }

        is LogsCommand.CopySelectedLogs -> {
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLogs(
                    lineIds = state.selectedItemIds,
                ),
            )
        }

        is LogsCommand.CopyFormattedText -> {
            state.withSideEffects(
                LogsSideEffect.CopyText(command.text),
            )
        }

        is LogsCommand.ToolbarClicked -> {
            val sideEffect = when {
                state.filters.isEmpty() -> LogsSideEffect.OpenFilters
                state.filters.size == 1 -> LogsSideEffect.OpenEditFilter(state.filters.first().id)
                else -> LogsSideEffect.OpenEditFilter(state.filters.last().id)
            }
            state.withSideEffects(sideEffect)
        }

        is LogsCommand.CreateFilterFromLog -> {
            val item = command.logLineItem
            state.withSideEffects(
                LogsSideEffect.OpenEditFilterFromLogLine(
                    uid = item.uid,
                    pid = item.pid,
                    tid = item.tid,
                    packageName = item.packageName,
                    tag = item.tag,
                    content = item.content,
                    level = item.level,
                ),
            )
        }

        is LogsCommand.OpenSearch -> {
            state.withSideEffects(LogsSideEffect.NavigateToSearch)
        }

        is LogsCommand.OpenFiltersScreen -> {
            state.withSideEffects(LogsSideEffect.OpenFilters)
        }

        is LogsCommand.OpenExtendedCopy -> {
            state.withSideEffects(LogsSideEffect.NavigateToExtendedCopy)
        }

        is LogsCommand.ClearLogs -> {
            state.withSideEffects(LogsSideEffect.ClearLogs)
        }

        is LogsCommand.RestartLogging -> {
            state.withSideEffects(LogsSideEffect.RestartLogging)
        }

        is LogsCommand.KillService -> {
            state.withSideEffects(LogsSideEffect.KillService)
        }
    }
}
