package com.f0x1d.logfox.feature.preferences.api.domain.logs

import kotlinx.coroutines.flow.Flow

interface GetLogsDisplayLimitFlowUseCase {
    operator fun invoke(): Flow<Int>
}
