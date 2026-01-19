package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetUseSeparateNotificationsChannelsForCrashesUseCase
import javax.inject.Inject

internal class GetUseSeparateNotificationsChannelsForCrashesUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetUseSeparateNotificationsChannelsForCrashesUseCase {

    override fun invoke(): Boolean = crashesSettingsRepository.useSeparateNotificationsChannelsForCrashes().value
}
