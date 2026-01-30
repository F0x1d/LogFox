package com.f0x1d.logfox.feature.logging.presentation.list.data

import com.f0x1d.logfox.feature.logging.presentation.list.domain.LogsSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

internal class LogsSelectionRepositoryImpl @Inject constructor() : LogsSelectionRepository {
    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    override fun update(ids: Set<Long>) {
        _selectedIds.value = ids
    }
}
