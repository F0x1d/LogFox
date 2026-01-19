package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow

interface NotificationsSettingsRepository {
    fun askedNotificationsPermission(): PreferenceStateFlow<Boolean>
}
