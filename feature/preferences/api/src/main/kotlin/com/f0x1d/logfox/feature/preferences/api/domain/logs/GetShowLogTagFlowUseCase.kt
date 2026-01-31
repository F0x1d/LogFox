package com.f0x1d.logfox.feature.preferences.api.domain.logs

import kotlinx.coroutines.flow.Flow

interface GetShowLogTagFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
