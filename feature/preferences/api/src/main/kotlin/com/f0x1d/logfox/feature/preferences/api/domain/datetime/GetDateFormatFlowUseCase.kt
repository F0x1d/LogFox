package com.f0x1d.logfox.feature.preferences.api.domain.datetime

import kotlinx.coroutines.flow.Flow

interface GetDateFormatFlowUseCase {
    operator fun invoke(): Flow<String>
}
