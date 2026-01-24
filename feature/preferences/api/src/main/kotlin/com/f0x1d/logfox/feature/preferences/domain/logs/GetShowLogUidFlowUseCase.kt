package com.f0x1d.logfox.feature.preferences.domain.logs

import kotlinx.coroutines.flow.Flow

interface GetShowLogUidFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
