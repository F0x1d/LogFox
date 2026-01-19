package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetResumeLoggingWithBottomTouchUseCase
import javax.inject.Inject

internal class GetResumeLoggingWithBottomTouchUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetResumeLoggingWithBottomTouchUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.resumeLoggingWithBottomTouch().value
}
