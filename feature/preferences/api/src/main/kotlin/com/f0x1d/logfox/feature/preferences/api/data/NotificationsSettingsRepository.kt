package com.f0x1d.logfox.feature.preferences.api.data

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow

interface NotificationsSettingsRepository {
    fun askedNotificationsPermission(): PreferenceStateFlow<Boolean>
}
