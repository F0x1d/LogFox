package com.f0x1d.logfox.feature.preferences.impl.data.notifications

import com.fredporciuncula.flow.preferences.Preference

internal interface NotificationsSettingsLocalDataSource {
    fun askedNotificationsPermission(): Preference<Boolean>
}
