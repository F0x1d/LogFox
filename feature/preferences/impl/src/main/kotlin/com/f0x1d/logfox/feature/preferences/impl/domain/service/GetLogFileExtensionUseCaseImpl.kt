package com.f0x1d.logfox.feature.preferences.impl.domain.service

import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetLogFileExtensionUseCase
import javax.inject.Inject

internal class GetLogFileExtensionUseCaseImpl @Inject constructor(
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : GetLogFileExtensionUseCase {

    override fun invoke(): String =
        if (serviceSettingsRepository.exportLogsAsTxt().value) "txt" else "log"
}
