package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.crashes.api.model.DisabledApp
import kotlinx.coroutines.flow.Flow

interface GetAllDisabledAppsFlowUseCase {
    operator fun invoke(): Flow<List<DisabledApp>>
}
