package com.f0x1d.logfox.feature.preferences.domain.logs

import kotlinx.coroutines.flow.Flow

interface GetShowLogTidFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
