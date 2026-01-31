package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

internal sealed interface PreferencesUICommand {
    data object Load : PreferencesUICommand

    data class NightThemeChanged(val themeIndex: Int) : PreferencesUICommand

    data object MonetEnabledChanged : PreferencesUICommand

    data class DateFormatChanged(val format: String?) : PreferencesUICommand

    data class TimeFormatChanged(val format: String?) : PreferencesUICommand

    data class LogsFormatChanged(val which: Int, val checked: Boolean) : PreferencesUICommand

    data class LogsUpdateIntervalChanged(val interval: Long?) : PreferencesUICommand

    data class LogsTextSizeChanged(val size: Int?) : PreferencesUICommand

    data class LogsDisplayLimitChanged(val limit: Int?) : PreferencesUICommand

    // Commands from effect handler
    data class PreferencesLoaded(
        val nightTheme: Int,
        val dateFormat: String,
        val timeFormat: String,
        val showLogDate: Boolean,
        val showLogTime: Boolean,
        val showLogUid: Boolean,
        val showLogPid: Boolean,
        val showLogTid: Boolean,
        val showLogPackage: Boolean,
        val showLogTag: Boolean,
        val showLogContent: Boolean,
        val logsUpdateInterval: Long,
        val logsTextSize: Int,
        val logsDisplayLimit: Int,
    ) : PreferencesUICommand
}
