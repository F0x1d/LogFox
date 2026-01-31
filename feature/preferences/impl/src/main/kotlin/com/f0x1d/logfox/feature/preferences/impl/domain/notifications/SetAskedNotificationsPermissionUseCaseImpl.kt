package com.f0x1d.logfox.feature.preferences.impl.domain.notifications

import com.f0x1d.logfox.feature.preferences.api.data.NotificationsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.notifications.SetAskedNotificationsPermissionUseCase
import javax.inject.Inject

internal class SetAskedNotificationsPermissionUseCaseImpl @Inject constructor(
    private val notificationsSettingsRepository: NotificationsSettingsRepository,
) : SetAskedNotificationsPermissionUseCase {

    override fun invoke(value: Boolean) {
        notificationsSettingsRepository.askedNotificationsPermission().set(value)
    }
}
