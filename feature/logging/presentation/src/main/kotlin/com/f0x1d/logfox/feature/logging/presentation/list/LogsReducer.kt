package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class LogsReducer @Inject constructor() : Reducer<LogsState, LogsCommand, LogsSideEffect> {

    override fun reduce(
        state: LogsState,
        command: LogsCommand,
    ): ReduceResult<LogsState, LogsSideEffect> = when (command) {
        is LogsCommand.LogsLoaded -> {
            state.copy(
                logs = command.logs,
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
            val newSelectedItems = state.selectedItems.toMutableSet().apply {
                if (command.selected) {
                    add(command.logLine)
                } else {
                    remove(command.logLine)
                }
            }
            state.copy(
                selectedItems = newSelectedItems,
                logsChanged = false,
            ).withSideEffects(
                LogsSideEffect.UpdateSelectedLogLines(
                    selectedLines = newSelectedItems.sortedBy { it.dateAndTime },
                ),
            )
        }

        is LogsCommand.SelectAll -> {
            val newSelectedItems = if (state.selectedItems.containsAll(state.logs)) {
                emptySet()
            } else {
                state.logs.toSet()
            }
            state.copy(
                selectedItems = newSelectedItems,
                logsChanged = false,
            ).withSideEffects(
                LogsSideEffect.UpdateSelectedLogLines(
                    selectedLines = newSelectedItems.sortedBy { it.dateAndTime },
                ),
            )
        }

        is LogsCommand.SelectedToRecording -> {
            state.withSideEffects(
                LogsSideEffect.CreateRecordingFromLines(
                    lines = state.selectedItems.sortedBy { it.dateAndTime },
                ),
                LogsSideEffect.NavigateToRecordings,
            )
        }

        is LogsCommand.ExportSelectedTo -> {
            state.withSideEffects(
                LogsSideEffect.ExportLogsTo(
                    uri = command.uri,
                    lines = state.selectedItems.sortedBy { it.dateAndTime },
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
            state.copy(
                selectedItems = emptySet(),
                logsChanged = false,
            ).withSideEffects(
                LogsSideEffect.UpdateSelectedLogLines(selectedLines = emptyList()),
            )
        }

        is LogsCommand.CopyLog -> {
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLog(command.logLine),
            )
        }

        is LogsCommand.CopySelectedLogs -> {
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLogs(
                    logLines = state.selectedItems.sortedBy { it.dateAndTime },
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
    }
}
