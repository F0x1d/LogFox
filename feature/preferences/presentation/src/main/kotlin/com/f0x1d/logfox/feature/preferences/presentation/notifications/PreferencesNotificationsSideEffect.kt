package com.f0x1d.logfox.feature.preferences.presentation.notifications

internal sealed interface PreferencesNotificationsSideEffect {
    // UI side effects - handled by Fragment
    data object OpenLoggingChannelSettings : PreferencesNotificationsSideEffect
    data object OpenAppNotificationSettings : PreferencesNotificationsSideEffect
}
