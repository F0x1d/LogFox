package com.f0x1d.logfox.feature.filters.api.domain

import com.f0x1d.logfox.feature.filters.api.model.UserFilter

interface CreateAllFiltersUseCase {
    suspend operator fun invoke(filters: List<UserFilter>)
}
