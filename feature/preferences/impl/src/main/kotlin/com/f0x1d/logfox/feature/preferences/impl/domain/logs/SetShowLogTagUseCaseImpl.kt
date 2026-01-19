package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetShowLogTagUseCase
import javax.inject.Inject

internal class SetShowLogTagUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogTagUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogTag().set(show)
    }
}
