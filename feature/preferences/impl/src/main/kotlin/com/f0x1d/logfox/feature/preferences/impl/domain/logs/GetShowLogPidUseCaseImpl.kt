package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogPidUseCase
import javax.inject.Inject

internal class GetShowLogPidUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogPidUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogPid().value
}
