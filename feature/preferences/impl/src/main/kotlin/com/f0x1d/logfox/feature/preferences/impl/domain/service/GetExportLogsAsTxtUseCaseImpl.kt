package com.f0x1d.logfox.feature.preferences.impl.domain.service

import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetExportLogsAsTxtUseCase
import javax.inject.Inject

internal class GetExportLogsAsTxtUseCaseImpl @Inject constructor(
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : GetExportLogsAsTxtUseCase {

    override fun invoke(): Boolean = serviceSettingsRepository.exportLogsAsTxt().value
}
