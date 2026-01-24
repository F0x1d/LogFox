package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetShowLogUidUseCase
import javax.inject.Inject

internal class SetShowLogUidUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetShowLogUidUseCase {

    override fun invoke(show: Boolean) {
        logsSettingsRepository.showLogUid().set(show)
    }
}
