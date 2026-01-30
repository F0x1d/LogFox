package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import javax.inject.Inject

internal class LogsReducer @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : Reducer<LogsState, LogsCommand, LogsSideEffect> {

    override fun reduce(
        state: LogsState,
        command: LogsCommand,
    ): ReduceResult<LogsState, LogsSideEffect> = when (command) {
        is LogsCommand.LogsLoaded -> {
            state.copy(
                logs = command.logs,
                query = command.query,
                filters = command.filters,
                selecting = command.selecting,
                selectedCount = command.selectedCount,
                logsChanged = true,
            ).noSideEffects()
        }

        is LogsCommand.PreferencesUpdated -> {
            state.copy(
                resumeLoggingWithBottomTouch = command.resumeLoggingWithBottomTouch,
                logsChanged = false,
            ).noSideEffects()
        }

        is LogsCommand.ItemClicked -> {
            if (state.selecting) {
                state.withSideEffects(LogsSideEffect.ToggleItemSelection(command.logLineId))
            } else {
                state.withSideEffects(LogsSideEffect.ToggleItemExpanded(command.logLineId))
            }
        }

        is LogsCommand.SelectLine -> {
            state.copy(selecting = true).withSideEffects(
                LogsSideEffect.SetItemSelected(command.logLineItem.logLineId, command.selected),
            )
        }

        is LogsCommand.SelectAll -> {
            val allIds = state.logs?.mapTo(mutableSetOf()) { it.logLineId } ?: emptySet()
            state.withSideEffects(LogsSideEffect.SelectAllItems(allIds))
        }

        is LogsCommand.ClearSelection -> {
            state.copy(selecting = false, selectedCount = 0).withSideEffects(
                LogsSideEffect.ClearSelection,
            )
        }

        is LogsCommand.SelectedToRecording -> {
            state.withSideEffects(
                LogsSideEffect.CreateRecordingFromLines,
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
                LogsSideEffect.ExportLogsTo(uri = command.uri),
            )
        }

        is LogsCommand.SwitchState -> {
            state.withSideEffects(LogsSideEffect.UpdatePaused(!state.paused))
        }

        is LogsCommand.Pause -> {
            if (state.paused) {
                state.noSideEffects()
            } else {
                state.withSideEffects(LogsSideEffect.UpdatePaused(true))
            }
        }

        is LogsCommand.Resume -> {
            if (!state.paused) {
                state.noSideEffects()
            } else {
                state.withSideEffects(LogsSideEffect.UpdatePaused(false))
            }
        }

        is LogsCommand.PausedStateUpdated -> {
            state.copy(paused = command.paused, logsChanged = false).noSideEffects()
        }

        is LogsCommand.CopyLog -> {
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLog(command.logLineItem.logLineId),
            )
        }

        is LogsCommand.CopySelectedLogs -> {
            state.withSideEffects(LogsSideEffect.FormatAndCopyLogs)
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
