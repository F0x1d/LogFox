package com.f0x1d.logfox.feature.filters.api.domain

import com.f0x1d.logfox.feature.database.model.UserFilter
import kotlinx.coroutines.flow.Flow

interface GetAllEnabledFiltersFlowUseCase {
    operator fun invoke(): Flow<List<UserFilter>>
}
