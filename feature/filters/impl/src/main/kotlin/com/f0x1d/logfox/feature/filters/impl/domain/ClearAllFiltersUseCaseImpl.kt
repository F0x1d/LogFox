package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.ClearAllFiltersUseCase
import javax.inject.Inject

internal class ClearAllFiltersUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : ClearAllFiltersUseCase {
    override suspend fun invoke() = filtersRepository.clear()
}
