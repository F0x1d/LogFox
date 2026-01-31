package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetUseSeparateNotificationsChannelsForCrashesFlowUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetUseSeparateNotificationsChannelsForCrashesFlowUseCase {

    override fun invoke(): Flow<Boolean> = crashesSettingsRepository.useSeparateNotificationsChannelsForCrashes()
}
