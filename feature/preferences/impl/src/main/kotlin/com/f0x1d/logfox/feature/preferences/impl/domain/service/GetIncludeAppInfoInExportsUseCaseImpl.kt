package com.f0x1d.logfox.feature.preferences.impl.domain.service

import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetIncludeAppInfoInExportsUseCase
import javax.inject.Inject

internal class GetIncludeAppInfoInExportsUseCaseImpl @Inject constructor(
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : GetIncludeAppInfoInExportsUseCase {

    override fun invoke(): Boolean = serviceSettingsRepository.includeAppInfoInExports().value
}
