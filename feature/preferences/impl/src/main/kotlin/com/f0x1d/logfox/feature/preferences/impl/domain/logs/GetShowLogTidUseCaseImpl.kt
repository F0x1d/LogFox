package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTidUseCase
import javax.inject.Inject

internal class GetShowLogTidUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogTidUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogTid().value
}
