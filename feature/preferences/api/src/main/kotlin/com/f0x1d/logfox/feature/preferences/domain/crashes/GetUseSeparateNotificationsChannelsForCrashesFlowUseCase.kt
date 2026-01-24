package com.f0x1d.logfox.feature.preferences.domain.crashes

import kotlinx.coroutines.flow.Flow

interface GetUseSeparateNotificationsChannelsForCrashesFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
