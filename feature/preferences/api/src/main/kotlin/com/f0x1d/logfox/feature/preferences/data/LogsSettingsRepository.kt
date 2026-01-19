package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import kotlinx.coroutines.flow.Flow

interface LogsSettingsRepository {
    companion object {
        const val LOGS_EXPANDED_DEFAULT = false
        const val LOGS_UPDATE_INTERVAL_DEFAULT = 300L
        const val LOGS_TEXT_SIZE_DEFAULT = 14
        const val LOGS_DISPLAY_LIMIT_DEFAULT = 10000
    }

    var logsUpdateInterval: Long
    val logsUpdateIntervalFlow: Flow<Long>

    var logsTextSize: Int
    val logsTextSizeFlow: Flow<Int>

    var logsDisplayLimit: Int
    val logsDisplayLimitFlow: Flow<Int>

    var logsExpanded: Boolean
    var resumeLoggingWithBottomTouch: Boolean
    var exportLogsInOriginalFormat: Boolean

    var showLogDate: Boolean
    var showLogTime: Boolean
    var showLogUid: Boolean
    var showLogPid: Boolean
    var showLogTid: Boolean
    var showLogPackage: Boolean
    var showLogTag: Boolean
    var showLogContent: Boolean

    val showLogValues: ShowLogValues
    val showLogValuesFlow: Flow<ShowLogValues>

    fun originalOf(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String
}
