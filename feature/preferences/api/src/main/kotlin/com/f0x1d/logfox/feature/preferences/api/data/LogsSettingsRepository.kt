package com.f0x1d.logfox.feature.preferences.api.data

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow

interface LogsSettingsRepository {
    companion object {
        const val LOGS_EXPANDED_DEFAULT = false
        const val LOGS_UPDATE_INTERVAL_DEFAULT = 300L
        const val LOGS_TEXT_SIZE_DEFAULT = 14
        const val LOGS_DISPLAY_LIMIT_DEFAULT = 10000
    }

    fun logsUpdateInterval(): PreferenceStateFlow<Long>
    fun logsTextSize(): PreferenceStateFlow<Int>
    fun logsDisplayLimit(): PreferenceStateFlow<Int>
    fun logsExpanded(): PreferenceStateFlow<Boolean>
    fun resumeLoggingWithBottomTouch(): PreferenceStateFlow<Boolean>
    fun exportLogsInOriginalFormat(): PreferenceStateFlow<Boolean>

    fun showLogDate(): PreferenceStateFlow<Boolean>
    fun showLogTime(): PreferenceStateFlow<Boolean>
    fun showLogUid(): PreferenceStateFlow<Boolean>
    fun showLogPid(): PreferenceStateFlow<Boolean>
    fun showLogTid(): PreferenceStateFlow<Boolean>
    fun showLogPackage(): PreferenceStateFlow<Boolean>
    fun showLogTag(): PreferenceStateFlow<Boolean>
    fun showLogContent(): PreferenceStateFlow<Boolean>
}
