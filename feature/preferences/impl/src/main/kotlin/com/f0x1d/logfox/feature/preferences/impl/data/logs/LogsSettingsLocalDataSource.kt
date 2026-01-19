package com.f0x1d.logfox.feature.preferences.impl.data.logs

import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import kotlinx.coroutines.flow.Flow

internal interface LogsSettingsLocalDataSource {
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
}
