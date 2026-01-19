package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetShowLogPackageUseCase
import javax.inject.Inject

internal class SetShowLogPackageUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogPackageUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogPackage().set(show)
    }
}
