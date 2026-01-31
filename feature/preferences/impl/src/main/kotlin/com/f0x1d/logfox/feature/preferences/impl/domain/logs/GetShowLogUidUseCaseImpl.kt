package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogUidUseCase
import javax.inject.Inject

internal class GetShowLogUidUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogUidUseCase {

    override fun invoke(): Boolean = logsSettingsRepository.showLogUid().value
}
