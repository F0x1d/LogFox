package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.CreateAllFiltersUseCase
import javax.inject.Inject

internal class CreateAllFiltersUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : CreateAllFiltersUseCase {
    override suspend fun invoke(filters: List<UserFilter>) = filtersRepository.createAll(filters)
}
