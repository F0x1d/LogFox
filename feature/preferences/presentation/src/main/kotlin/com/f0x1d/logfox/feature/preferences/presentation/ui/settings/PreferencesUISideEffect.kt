package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

internal sealed interface PreferencesUISideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadPreferences : PreferencesUISideEffect
    data class SaveNightTheme(val themeIndex: Int) : PreferencesUISideEffect
    data class SaveDateFormat(val format: String) : PreferencesUISideEffect
    data class SaveTimeFormat(val format: String) : PreferencesUISideEffect
    data class SaveLogsFormat(val which: Int, val checked: Boolean) : PreferencesUISideEffect
    data class SaveLogsUpdateInterval(val interval: Long) : PreferencesUISideEffect
    data class SaveLogsTextSize(val size: Int) : PreferencesUISideEffect
    data class SaveLogsDisplayLimit(val limit: Int) : PreferencesUISideEffect

    // UI side effects - handled by Fragment
    data object RecreateActivity : PreferencesUISideEffect
}
