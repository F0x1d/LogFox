package com.f0x1d.logfox.feature.logging.api.domain

import kotlinx.coroutines.flow.Flow

interface GetQueryFlowUseCase {
    operator fun invoke(): Flow<String?>
}
