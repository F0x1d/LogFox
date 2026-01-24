package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesUseCase
import javax.inject.Inject

internal class GetUseSeparateNotificationsChannelsForCrashesUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetUseSeparateNotificationsChannelsForCrashesUseCase {

    override fun invoke(): Boolean = crashesSettingsRepository.useSeparateNotificationsChannelsForCrashes().value
}
