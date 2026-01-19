package com.f0x1d.logfox.feature.preferences.presentation.notifications

internal data class PreferencesNotificationsState(
    val hasNotificationsPermission: Boolean = true,
    val notificationsChannelsAvailable: Boolean = false,
)
