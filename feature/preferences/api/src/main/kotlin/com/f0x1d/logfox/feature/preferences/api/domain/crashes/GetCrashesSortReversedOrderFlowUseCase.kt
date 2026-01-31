package com.f0x1d.logfox.feature.preferences.api.domain.crashes

import kotlinx.coroutines.flow.Flow

interface GetCrashesSortReversedOrderFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
