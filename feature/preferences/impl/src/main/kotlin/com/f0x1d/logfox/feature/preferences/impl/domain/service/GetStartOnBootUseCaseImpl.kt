package com.f0x1d.logfox.feature.preferences.impl.domain.service

import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.service.GetStartOnBootUseCase
import javax.inject.Inject

internal class GetStartOnBootUseCaseImpl @Inject constructor(
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : GetStartOnBootUseCase {

    override fun invoke(): Boolean = serviceSettingsRepository.startOnBoot().value
}
