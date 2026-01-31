package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetOpenCrashesOnStartupUseCase
import javax.inject.Inject

internal class GetOpenCrashesOnStartupUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetOpenCrashesOnStartupUseCase {

    override fun invoke(): Boolean = crashesSettingsRepository.openCrashesOnStartup().value
}
