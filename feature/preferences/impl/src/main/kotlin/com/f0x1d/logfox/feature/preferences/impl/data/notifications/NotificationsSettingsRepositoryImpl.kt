package com.f0x1d.logfox.feature.preferences.impl.data.notifications

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.data.NotificationsSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationsSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: NotificationsSettingsLocalDataSource,
) : NotificationsSettingsRepository {

    override fun askedNotificationsPermission(): PreferenceStateFlow<Boolean> =
        localDataSource.askedNotificationsPermission().asPreferenceStateFlow()
}
