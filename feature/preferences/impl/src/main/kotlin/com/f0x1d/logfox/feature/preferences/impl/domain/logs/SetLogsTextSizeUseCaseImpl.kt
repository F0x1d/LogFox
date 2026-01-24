package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.SetLogsTextSizeUseCase
import javax.inject.Inject

internal class SetLogsTextSizeUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : SetLogsTextSizeUseCase {

    override fun invoke(size: Int) {
        logsSettingsRepository.logsTextSize().set(size)
    }
}
