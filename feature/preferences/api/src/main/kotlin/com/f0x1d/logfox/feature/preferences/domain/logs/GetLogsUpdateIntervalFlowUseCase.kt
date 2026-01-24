package com.f0x1d.logfox.feature.preferences.domain.logs

import kotlinx.coroutines.flow.Flow

interface GetLogsUpdateIntervalFlowUseCase {
    operator fun invoke(): Flow<Long>
}
