package com.f0x1d.logfox.feature.filters.edit.presentation

import com.f0x1d.logfox.database.entity.UserFilter

data class EditFilterState(
    val filter: UserFilter? = null,
    val including: Boolean = true,
    val enabledLogLevels: List<Boolean> = emptyList(),
)
