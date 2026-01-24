package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetWrapCrashLogLinesUseCase
import javax.inject.Inject

internal class GetWrapCrashLogLinesUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetWrapCrashLogLinesUseCase {

    override fun invoke(): Boolean = crashesSettingsRepository.wrapCrashLogLines().value
}
