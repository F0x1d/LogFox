package com.f0x1d.logfox.feature.logging.list.presentation

import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.logline.LogLine

data class LogsState(
    val logs: List<LogLine> = emptyList(),
    val logsChanged: Boolean = true,
    val paused: Boolean = false,
    val query: String? = null,
    val filters: List<UserFilter> = emptyList(),
    val selectedItems: Set<LogLine> = emptySet(),
)
