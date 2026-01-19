package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchUseCase
import javax.inject.Inject

internal class GetResumeLoggingWithBottomTouchUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetResumeLoggingWithBottomTouchUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.resumeLoggingWithBottomTouch().value
}
