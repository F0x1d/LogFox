package com.f0x1d.logfox.feature.filters.presentation.list

import com.f0x1d.logfox.feature.filters.api.model.UserFilter

data class FiltersState(val filters: List<UserFilter> = emptyList())
