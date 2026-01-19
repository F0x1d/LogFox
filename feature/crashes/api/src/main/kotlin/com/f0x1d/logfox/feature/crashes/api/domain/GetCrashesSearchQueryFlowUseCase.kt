package com.f0x1d.logfox.feature.crashes.api.domain

import kotlinx.coroutines.flow.Flow

interface GetCrashesSearchQueryFlowUseCase {
    operator fun invoke(): Flow<String>
}
