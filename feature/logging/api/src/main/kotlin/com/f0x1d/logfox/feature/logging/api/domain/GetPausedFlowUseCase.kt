package com.f0x1d.logfox.feature.logging.api.domain

import kotlinx.coroutines.flow.Flow

interface GetPausedFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
