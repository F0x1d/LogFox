package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import javax.inject.Inject

internal class LogLineFormatterRepositoryImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : LogLineFormatterRepository {

    override fun format(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String = if (logsSettingsRepository.exportLogsInOriginalFormat().value) {
        logLine.originalContent
    } else {
        logLine.formatOriginal(
            values = getShowLogValues(),
            formatDate = formatDate,
            formatTime = formatTime,
        )
    }

    private fun getShowLogValues(): ShowLogValues = ShowLogValues(
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
