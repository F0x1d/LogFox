package com.f0x1d.logfox.feature.preferences.impl.data.notifications

import android.content.Context
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationsSettingsLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : BasePreferences(context),
        NotificationsSettingsLocalDataSource {
        override var askedNotificationsPermission
            get() = get(KEY_ASKED_NOTIFICATIONS_PERMISSION, false)
            set(value) = put(KEY_ASKED_NOTIFICATIONS_PERMISSION, value)

        private companion object {
            const val KEY_ASKED_NOTIFICATIONS_PERMISSION = "pref_asked_notifications_permission"
        }
    }
