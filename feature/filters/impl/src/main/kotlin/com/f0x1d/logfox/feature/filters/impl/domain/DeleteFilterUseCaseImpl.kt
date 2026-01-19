package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.DeleteFilterUseCase
import javax.inject.Inject

internal class DeleteFilterUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : DeleteFilterUseCase {
    override suspend fun invoke(userFilter: UserFilter) = filtersRepository.delete(userFilter)
}
