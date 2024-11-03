package com.f0x1d.logfox.feature.filters.list.presentation

import com.f0x1d.logfox.database.entity.UserFilter

data class FiltersState(
    val filters: List<UserFilter> = emptyList(),
)
