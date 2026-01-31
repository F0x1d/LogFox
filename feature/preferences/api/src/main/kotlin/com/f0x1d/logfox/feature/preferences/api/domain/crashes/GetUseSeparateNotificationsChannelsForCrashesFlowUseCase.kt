package com.f0x1d.logfox.feature.preferences.api.domain.crashes

import kotlinx.coroutines.flow.Flow

interface GetUseSeparateNotificationsChannelsForCrashesFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
