package com.f0x1d.logfox.feature.preferences.presentation.menu

internal sealed interface PreferencesMenuCommand {
    data object UISettingsClicked : PreferencesMenuCommand
    data object ServiceSettingsClicked : PreferencesMenuCommand
    data object CrashesSettingsClicked : PreferencesMenuCommand
    data object NotificationsSettingsClicked : PreferencesMenuCommand
    data object LinksClicked : PreferencesMenuCommand
    data object ShareLogsClicked : PreferencesMenuCommand
}
