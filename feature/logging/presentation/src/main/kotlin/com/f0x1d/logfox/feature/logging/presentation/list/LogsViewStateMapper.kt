package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.presentation.list.model.toPresentationModel
import javax.inject.Inject

internal class LogsViewStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) {

    fun map(state: LogsState): LogsViewState {
        val filteredLogs = state.logs?.filterAndSearch(
            filters = state.filters,
            query = state.query,
            caseSensitive = state.caseSensitive,
        )

        return LogsViewState(
            logs = filteredLogs?.map { line ->
                line.toPresentationModel(
                    displayText = line.formatOriginal(
                        values = state.showLogValues,
                        formatDate = dateTimeFormatter::formatDate,
                        formatTime = dateTimeFormatter::formatTime,
                    ),
                    expanded = state.expandedOverrides.getOrElse(line.id) { state.logsExpanded },
                    selected = line.id in state.selectedIds,
                    textSize = state.textSize.toFloat(),
                )
            },
            logsChanged = state.logsChanged,
            paused = state.paused,
            query = state.query,
            filters = state.filters,
            selecting = state.selectedIds.isNotEmpty(),
            selectedCount = state.selectedIds.size,
            resumeLoggingWithBottomTouch = state.resumeLoggingWithBottomTouch,
        )
    }
}
