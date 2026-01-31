package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTagUseCase
import javax.inject.Inject

internal class GetShowLogTagUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogTagUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogTag().value
}
