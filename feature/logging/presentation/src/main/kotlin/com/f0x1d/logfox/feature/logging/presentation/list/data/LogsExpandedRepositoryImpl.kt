package com.f0x1d.logfox.feature.logging.presentation.list.data

import com.f0x1d.logfox.feature.logging.presentation.list.domain.LogsExpandedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

internal class LogsExpandedRepositoryImpl @Inject constructor() : LogsExpandedRepository {
    private val _expandedOverrides = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    override val expandedOverrides: StateFlow<Map<Long, Boolean>> = _expandedOverrides.asStateFlow()

    override fun update(overrides: Map<Long, Boolean>) {
        _expandedOverrides.value = overrides
    }
}
