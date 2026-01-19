package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesUseCase
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import javax.inject.Inject

internal class GetShowLogValuesUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogValuesUseCase {

    override fun invoke(): ShowLogValues = ShowLogValues(
        date = logsSettingsRepository.showLogDate().value,
        time = logsSettingsRepository.showLogTime().value,
        uid = logsSettingsRepository.showLogUid().value,
        pid = logsSettingsRepository.showLogPid().value,
        tid = logsSettingsRepository.showLogTid().value,
        packageName = logsSettingsRepository.showLogPackage().value,
        tag = logsSettingsRepository.showLogTag().value,
        content = logsSettingsRepository.showLogContent().value,
    )
}
