package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetShowLogTidUseCase
import javax.inject.Inject

internal class SetShowLogTidUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogTidUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogTid().set(show)
    }
}
