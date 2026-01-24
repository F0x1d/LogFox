package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetShowLogContentUseCase
import javax.inject.Inject

internal class SetShowLogContentUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogContentUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogContent().set(show)
    }
}
