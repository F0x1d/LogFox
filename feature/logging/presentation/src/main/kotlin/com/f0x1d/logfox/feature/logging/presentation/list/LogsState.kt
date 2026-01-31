package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

data class LogsState(
    val logs: List<LogLine>? = null,
    val pausedLogs: List<LogLine>? = null,
    val paused: Boolean = false,
    val query: String? = null,
    val caseSensitive: Boolean = false,
    val filters: List<UserFilter> = emptyList(),
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
    val selectedIds: Set<Long> = emptySet(),
    val expandedOverrides: Map<Long, Boolean> = emptyMap(),
    val logsExpanded: Boolean,
    val textSize: Int,
    val logsChanged: Boolean = true,
    val resumeLoggingWithBottomTouch: Boolean,
) {
    val visibleLogs: List<LogLine>? get() = if (paused) (pausedLogs ?: logs) else logs
}
