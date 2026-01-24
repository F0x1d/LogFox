package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

internal data class PreferencesUIState(
    val nightTheme: Int = -1,
    val dateFormat: String = "",
    val timeFormat: String = "",
    val showLogValues: ShowLogValues =
        ShowLogValues(
            date = false,
            time = false,
            uid = false,
            pid = false,
            tid = false,
            packageName = false,
            tag = true,
            content = true,
        ),
    val logsUpdateInterval: Long = 0,
    val logsTextSize: Int = 0,
    val logsDisplayLimit: Int = 0,
)
