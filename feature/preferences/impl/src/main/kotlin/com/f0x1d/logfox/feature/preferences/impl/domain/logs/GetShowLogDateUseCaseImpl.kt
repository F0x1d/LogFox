package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogDateUseCase
import javax.inject.Inject

internal class GetShowLogDateUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogDateUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogDate().value
}
