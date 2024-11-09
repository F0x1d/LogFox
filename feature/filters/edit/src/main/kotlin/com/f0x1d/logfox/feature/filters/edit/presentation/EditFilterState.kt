package com.f0x1d.logfox.feature.filters.edit.presentation

import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.logline.LogLevel

data class EditFilterState(
    val filter: UserFilter? = null,
    val including: Boolean = true,
    val enabledLogLevels: List<Boolean> = List(LogLevel.entries.size) { false },
)
