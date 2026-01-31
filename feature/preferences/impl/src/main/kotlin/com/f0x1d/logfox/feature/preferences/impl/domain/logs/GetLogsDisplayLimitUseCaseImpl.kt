package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsDisplayLimitUseCase
import javax.inject.Inject

internal class GetLogsDisplayLimitUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsDisplayLimitUseCase {

    override fun invoke(): Int = logsSettingsRepository.logsDisplayLimit().value
}
