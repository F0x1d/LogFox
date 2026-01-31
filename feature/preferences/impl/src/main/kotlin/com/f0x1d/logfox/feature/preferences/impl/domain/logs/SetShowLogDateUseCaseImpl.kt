package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogDateUseCase
import javax.inject.Inject

internal class SetShowLogDateUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogDateUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogDate().set(show)
    }
}
