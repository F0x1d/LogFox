package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import javax.inject.Inject

internal class FormatLogLineUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : FormatLogLineUseCase {

    override fun invoke(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String = if (logsSettingsRepository.exportLogsInOriginalFormat().value) {
        logLine.originalContent
    } else {
        logLine.formatOriginal(
            values = showLogValues(),
            formatDate = formatDate,
            formatTime = formatTime,
        )
    }

    override fun showLogValues(): ShowLogValues = ShowLogValues(
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
