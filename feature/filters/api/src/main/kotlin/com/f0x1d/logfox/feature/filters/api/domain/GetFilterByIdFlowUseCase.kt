package com.f0x1d.logfox.feature.filters.api.domain

import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import kotlinx.coroutines.flow.Flow

interface GetFilterByIdFlowUseCase {
    operator fun invoke(id: Long): Flow<UserFilter?>
}
