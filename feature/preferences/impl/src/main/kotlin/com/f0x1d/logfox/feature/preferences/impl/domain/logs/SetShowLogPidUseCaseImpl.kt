package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogPidUseCase
import javax.inject.Inject

internal class SetShowLogPidUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogPidUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogPid().set(show)
    }
}
