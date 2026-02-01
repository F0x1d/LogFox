package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.SetWrapCrashLogLinesUseCase
import javax.inject.Inject

internal class SetWrapCrashLogLinesUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : SetWrapCrashLogLinesUseCase {

    override fun invoke(wrap: Boolean) {
        crashesSettingsRepository.wrapCrashLogLines().set(wrap)
    }
}
