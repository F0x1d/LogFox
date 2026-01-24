package com.f0x1d.logfox.feature.preferences.domain.crashes

import kotlinx.coroutines.flow.Flow

interface GetWrapCrashLogLinesFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
