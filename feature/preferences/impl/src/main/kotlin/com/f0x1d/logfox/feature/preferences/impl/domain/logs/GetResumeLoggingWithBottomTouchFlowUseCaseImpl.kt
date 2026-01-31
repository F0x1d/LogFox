package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetResumeLoggingWithBottomTouchFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetResumeLoggingWithBottomTouchFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.resumeLoggingWithBottomTouch()
}
