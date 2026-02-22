package com.f0x1d.logfox.feature.preferences.impl.domain.service

import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetStopLoggingOnBackExitUseCase
import javax.inject.Inject

internal class GetStopLoggingOnBackExitUseCaseImpl @Inject constructor(
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : GetStopLoggingOnBackExitUseCase {

    override fun invoke(): Boolean = serviceSettingsRepository.stopLoggingOnBackExit().value
}
