package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetLogsDisplayLimitUseCase
import javax.inject.Inject

internal class SetLogsDisplayLimitUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetLogsDisplayLimitUseCase {

    override fun invoke(limit: Int) {
        logsSettingsRepository.logsDisplayLimit().set(limit)
    }
}
