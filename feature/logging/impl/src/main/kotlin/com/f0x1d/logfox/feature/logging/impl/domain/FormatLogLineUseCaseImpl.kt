package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import javax.inject.Inject

internal class FormatLogLineUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
    private val getShowLogValuesUseCase: GetShowLogValuesUseCase,
) : FormatLogLineUseCase {

    override fun invoke(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String = if (logsSettingsRepository.exportLogsInOriginalFormat().value) {
        logLine.originalContent
    } else {
        logLine.formatOriginal(
            values = getShowLogValuesUseCase(),
            formatDate = formatDate,
            formatTime = formatTime,
        )
    }
}
