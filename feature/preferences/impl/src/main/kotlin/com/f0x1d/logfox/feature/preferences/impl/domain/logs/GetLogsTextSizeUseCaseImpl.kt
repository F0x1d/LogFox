package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeUseCase
import javax.inject.Inject

internal class GetLogsTextSizeUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsTextSizeUseCase {

    override fun invoke(): Int = logsSettingsRepository.logsTextSize().value
}
