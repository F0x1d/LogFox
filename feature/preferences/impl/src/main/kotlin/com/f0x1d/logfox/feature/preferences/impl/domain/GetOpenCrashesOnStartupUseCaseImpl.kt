package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetOpenCrashesOnStartupUseCase
import javax.inject.Inject

internal class GetOpenCrashesOnStartupUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetOpenCrashesOnStartupUseCase {

    override fun invoke(): Boolean = crashesSettingsRepository.openCrashesOnStartup().value
}
