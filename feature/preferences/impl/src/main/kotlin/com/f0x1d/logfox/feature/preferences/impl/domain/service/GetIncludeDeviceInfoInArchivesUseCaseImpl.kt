package com.f0x1d.logfox.feature.preferences.impl.domain.service

import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import javax.inject.Inject

internal class GetIncludeDeviceInfoInArchivesUseCaseImpl @Inject constructor(
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : GetIncludeDeviceInfoInArchivesUseCase {

    override fun invoke(): Boolean = serviceSettingsRepository.includeDeviceInfoInArchives().value
}
