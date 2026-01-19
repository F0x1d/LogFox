package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetLogsUpdateIntervalUseCase
import javax.inject.Inject

internal class GetLogsUpdateIntervalUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsUpdateIntervalUseCase {

    override fun invoke(): Long = logsSettingsRepository.logsUpdateInterval().value
}
