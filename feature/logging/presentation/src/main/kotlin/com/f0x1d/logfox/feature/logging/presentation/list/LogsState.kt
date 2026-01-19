package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLine

data class LogsState(
    val logs: List<LogLine> = emptyList(),
    val logsChanged: Boolean = true,
    val paused: Boolean = false,
    val query: String? = null,
    val filters: List<UserFilter> = emptyList(),
    val selectedItems: Set<LogLine> = emptySet(),
)
