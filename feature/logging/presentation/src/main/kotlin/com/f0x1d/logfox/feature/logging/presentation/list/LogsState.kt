package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

data class LogsState(
    val logs: List<LogLine>?,
    val paused: Boolean,
    val query: String?,
    val caseSensitive: Boolean,
    val filters: List<UserFilter>,
    val showLogValues: ShowLogValues,
    val selectedIds: Set<Long>,
    val expandedOverrides: Map<Long, Boolean>,
    val logsExpanded: Boolean,
    val textSize: Int,
    val logsChanged: Boolean,
    val resumeLoggingWithBottomTouch: Boolean,
)
