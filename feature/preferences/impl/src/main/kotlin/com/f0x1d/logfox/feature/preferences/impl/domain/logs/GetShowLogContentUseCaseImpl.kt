package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogContentUseCase
import javax.inject.Inject

internal class GetShowLogContentUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogContentUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogContent().value
}
