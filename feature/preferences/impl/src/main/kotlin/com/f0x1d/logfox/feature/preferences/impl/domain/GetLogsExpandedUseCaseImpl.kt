package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetLogsExpandedUseCase
import javax.inject.Inject

internal class GetLogsExpandedUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsExpandedUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.logsExpanded().value
}
