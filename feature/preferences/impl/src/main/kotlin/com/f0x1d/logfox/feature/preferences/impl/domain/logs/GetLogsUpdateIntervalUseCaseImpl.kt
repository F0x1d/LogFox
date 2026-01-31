package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsUpdateIntervalUseCase
import javax.inject.Inject

internal class GetLogsUpdateIntervalUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsUpdateIntervalUseCase {

    override fun invoke(): Long = logsSettingsRepository.logsUpdateInterval().value
}
