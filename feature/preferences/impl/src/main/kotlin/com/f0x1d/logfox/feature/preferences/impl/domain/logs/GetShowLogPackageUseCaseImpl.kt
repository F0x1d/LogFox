package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogPackageUseCase
import javax.inject.Inject

internal class GetShowLogPackageUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogPackageUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogPackage().value
}
