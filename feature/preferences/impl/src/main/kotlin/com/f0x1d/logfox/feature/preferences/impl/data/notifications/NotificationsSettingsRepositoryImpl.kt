package com.f0x1d.logfox.feature.preferences.impl.data.notifications

import com.f0x1d.logfox.feature.preferences.data.NotificationsSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationsSettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: NotificationsSettingsLocalDataSource,
    ) : NotificationsSettingsRepository {
        override var askedNotificationsPermission: Boolean
            get() = localDataSource.askedNotificationsPermission
            set(value) {
                localDataSource.askedNotificationsPermission = value
            }
    }
