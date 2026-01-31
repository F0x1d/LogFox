package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsUpdateIntervalUseCase
import javax.inject.Inject

internal class SetLogsUpdateIntervalUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetLogsUpdateIntervalUseCase {

    override fun invoke(interval: Long) {
        logsSettingsRepository.logsUpdateInterval().set(interval)
    }
}
