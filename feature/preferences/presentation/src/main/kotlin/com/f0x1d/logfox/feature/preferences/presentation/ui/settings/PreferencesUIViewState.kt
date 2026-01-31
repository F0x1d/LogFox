package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

internal data class PreferencesUIViewState(
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
)
