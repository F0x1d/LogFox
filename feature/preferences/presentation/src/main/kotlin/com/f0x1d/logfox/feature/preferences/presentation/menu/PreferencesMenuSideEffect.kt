package com.f0x1d.logfox.feature.preferences.presentation.menu

import java.io.File

internal sealed interface PreferencesMenuSideEffect {
    // UI side effects - handled by Fragment
    data object NavigateToUISettings : PreferencesMenuSideEffect
    data object NavigateToServiceSettings : PreferencesMenuSideEffect
    data object NavigateToCrashesSettings : PreferencesMenuSideEffect
    data object NavigateToNotificationsSettings : PreferencesMenuSideEffect
    data object NavigateToLinks : PreferencesMenuSideEffect
    data class ShareLogs(val file: File) : PreferencesMenuSideEffect
}
