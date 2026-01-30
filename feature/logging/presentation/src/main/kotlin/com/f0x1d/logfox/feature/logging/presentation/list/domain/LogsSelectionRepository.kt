package com.f0x1d.logfox.feature.logging.presentation.list.domain

import kotlinx.coroutines.flow.StateFlow

internal interface LogsSelectionRepository {
    val selectedIds: StateFlow<Set<Long>>
    fun update(ids: Set<Long>)
}
