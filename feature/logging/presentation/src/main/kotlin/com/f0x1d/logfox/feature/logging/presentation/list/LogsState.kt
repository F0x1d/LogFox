package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem

data class LogsState(
    val logs: List<LogLineItem>? = null,
    val logsChanged: Boolean = true,
    val paused: Boolean = false,
    val query: String? = null,
    val filters: List<UserFilter> = emptyList(),
    val selecting: Boolean = false,
    val selectedCount: Int = 0,
    val resumeLoggingWithBottomTouch: Boolean,
)
