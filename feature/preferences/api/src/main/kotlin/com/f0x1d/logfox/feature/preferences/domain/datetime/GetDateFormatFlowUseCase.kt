package com.f0x1d.logfox.feature.preferences.domain.datetime

import kotlinx.coroutines.flow.Flow

interface GetDateFormatFlowUseCase {
    operator fun invoke(): Flow<String>
}
