package com.f0x1d.logfox.feature.preferences.impl.data.logs

import com.fredporciuncula.flow.preferences.Preference

internal interface LogsSettingsLocalDataSource {
    fun logsUpdateInterval(): Preference<Long>
    fun logsTextSize(): Preference<Int>
    fun logsDisplayLimit(): Preference<Int>
    fun logsExpanded(): Preference<Boolean>
    fun resumeLoggingWithBottomTouch(): Preference<Boolean>
    fun exportLogsInOriginalFormat(): Preference<Boolean>

    fun showLogDate(): Preference<Boolean>
    fun showLogTime(): Preference<Boolean>
    fun showLogUid(): Preference<Boolean>
    fun showLogPid(): Preference<Boolean>
    fun showLogTid(): Preference<Boolean>
    fun showLogPackage(): Preference<Boolean>
    fun showLogTag(): Preference<Boolean>
    fun showLogContent(): Preference<Boolean>
}
