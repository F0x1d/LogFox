package com.f0x1d.logfox.feature.preferences.domain.datetime

import kotlinx.coroutines.flow.Flow

interface GetTimeFormatFlowUseCase {
    operator fun invoke(): Flow<String>
}
