package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogTimeUseCase
import javax.inject.Inject

internal class GetShowLogTimeUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogTimeUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogTime().value
}
