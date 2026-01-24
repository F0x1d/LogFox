package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.GetFilterByIdFlowUseCase
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetFilterByIdFlowUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : GetFilterByIdFlowUseCase {
    override fun invoke(id: Long): Flow<UserFilter?> = filtersRepository.getByIdAsFlow(id)
}
