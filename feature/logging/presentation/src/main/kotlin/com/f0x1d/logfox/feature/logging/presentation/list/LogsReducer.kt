package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import javax.inject.Inject

internal class LogsReducer @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : Reducer<LogsState, LogsCommand, LogsSideEffect> {

    override fun reduce(
        state: LogsState,
        command: LogsCommand,
    ): ReduceResult<LogsState, LogsSideEffect> = when (command) {
        is LogsCommand.LogsLoaded -> {
            if (state.paused) {
                val paramsChanged = command.query != state.query ||
                    command.filters != state.filters ||
                    command.caseSensitive != state.caseSensitive ||
                    command.showLogValues != state.showLogValues

                if (paramsChanged) {
                    state.copy(
                        query = command.query,
                        caseSensitive = command.caseSensitive,
                        filters = command.filters,
                        showLogValues = command.showLogValues,
                        logsChanged = true,
                    ).noSideEffects()
                } else {
                    state.noSideEffects()
                }
            } else {
                state.copy(
                    logs = command.logs,
                    query = command.query,
                    caseSensitive = command.caseSensitive,
                    filters = command.filters,
                    showLogValues = command.showLogValues,
                    logsChanged = true,
                ).noSideEffects()
            }
        }

        is LogsCommand.PreferencesUpdated -> {
            state.copy(
                resumeLoggingWithBottomTouch = command.resumeLoggingWithBottomTouch,
                textSize = command.textSize,
                logsExpanded = command.logsExpanded,
                logsChanged = command.textSize != state.textSize ||
                    command.logsExpanded != state.logsExpanded,
            ).noSideEffects()
        }

        is LogsCommand.ItemClicked -> {
            if (state.selectedIds.isNotEmpty()) {
                val newIds = state.selectedIds.toggle(command.logLineId)
                state.copy(selectedIds = newIds, logsChanged = true)
                    .withSideEffects(LogsSideEffect.SyncSelectedLines(state.logsByIds(newIds)))
            } else {
                val currentExpanded = state.expandedOverrides.getOrElse(command.logLineId) {
                    state.logsExpanded
                }
                state.copy(
                    expandedOverrides = state.expandedOverrides + (command.logLineId to !currentExpanded),
                    logsChanged = true,
                ).noSideEffects()
            }
        }

        is LogsCommand.SelectLine -> {
            val newIds = if (command.selected) {
                state.selectedIds + command.logLineId
            } else {
                state.selectedIds - command.logLineId
            }
            state.copy(selectedIds = newIds, logsChanged = true)
                .withSideEffects(LogsSideEffect.SyncSelectedLines(state.logsByIds(newIds)))
        }

        is LogsCommand.SelectAll -> {
            val newIds = if (state.selectedIds == command.visibleIds) emptySet() else command.visibleIds
            state.copy(selectedIds = newIds, logsChanged = true)
                .withSideEffects(LogsSideEffect.SyncSelectedLines(state.logsByIds(newIds)))
        }

        is LogsCommand.ClearSelection -> {
            state.copy(selectedIds = emptySet(), logsChanged = true)
                .withSideEffects(LogsSideEffect.SyncSelectedLines(emptyList()))
        }

        is LogsCommand.SelectedToRecording -> {
            state.withSideEffects(
                LogsSideEffect.CreateRecordingFromLines(lines = state.selectedLines()),
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
                    lines = state.selectedLines(),
                ),
            )
        }

        is LogsCommand.SwitchState -> {
            state.copy(
                paused = !state.paused,
                logsChanged = state.paused,
            ).noSideEffects()
        }

        is LogsCommand.Pause -> {
            if (state.paused) {
                state.noSideEffects()
            } else {
                state.copy(paused = true, logsChanged = false).noSideEffects()
            }
        }

        is LogsCommand.Resume -> {
            if (!state.paused) {
                state.noSideEffects()
            } else {
                state.copy(paused = false, logsChanged = true).noSideEffects()
            }
        }

        is LogsCommand.CopyLog -> {
            val logLine = state.logs
                .firstOrNull { it.id == command.logLineId }
                ?: return@reduce state.noSideEffects()
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLog(logLine),
            )
        }

        is LogsCommand.CopySelectedLogs -> {
            state.withSideEffects(
                LogsSideEffect.FormatAndCopyLogs(lines = state.selectedLines()),
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
            val logLine = state.logs
                .firstOrNull { it.id == command.logLineId }
                ?: return@reduce state.noSideEffects()
            state.withSideEffects(
                LogsSideEffect.OpenEditFilterFromLogLine(
                    uid = logLine.uid,
                    pid = logLine.pid,
                    tid = logLine.tid,
                    packageName = logLine.packageName,
                    tag = logLine.tag,
                    content = logLine.content,
                    level = logLine.level,
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

    private fun LogsState.selectedLines(): List<LogLine> =
        logsByIds(selectedIds)

    private fun LogsState.logsByIds(ids: Set<Long>): List<LogLine> =
        logs.filter { it.id in ids }.sortedBy { it.dateAndTime }

    private fun Set<Long>.toggle(id: Long): Set<Long> =
        if (id in this) this - id else this + id
}
