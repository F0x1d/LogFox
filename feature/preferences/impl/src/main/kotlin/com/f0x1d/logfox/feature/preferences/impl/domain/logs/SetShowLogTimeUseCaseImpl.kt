package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetShowLogTimeUseCase
import javax.inject.Inject

internal class SetShowLogTimeUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogTimeUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogTime().set(show)
    }
}
