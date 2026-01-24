package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetAllEnabledFiltersFlowUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : GetAllEnabledFiltersFlowUseCase {
    override fun invoke(): Flow<List<UserFilter>> = filtersRepository.getAllEnabledAsFlow()
}
