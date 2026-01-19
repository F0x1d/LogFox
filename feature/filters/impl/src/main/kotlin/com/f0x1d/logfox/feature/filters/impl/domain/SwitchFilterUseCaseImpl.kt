package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.SwitchFilterUseCase
import javax.inject.Inject

internal class SwitchFilterUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : SwitchFilterUseCase {
    override suspend fun invoke(userFilter: UserFilter, checked: Boolean) = filtersRepository.switch(userFilter, checked)
}
