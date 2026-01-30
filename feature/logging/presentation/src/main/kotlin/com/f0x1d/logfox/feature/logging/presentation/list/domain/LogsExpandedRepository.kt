package com.f0x1d.logfox.feature.logging.presentation.list.domain

import kotlinx.coroutines.flow.StateFlow

internal interface LogsExpandedRepository {
    val expandedOverrides: StateFlow<Map<Long, Boolean>>
    fun update(overrides: Map<Long, Boolean>)
}
