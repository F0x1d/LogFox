package com.f0x1d.logfox.feature.preferences.impl.domain.notifications

import com.f0x1d.logfox.feature.preferences.api.data.NotificationsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.notifications.GetAskedNotificationsPermissionUseCase
import javax.inject.Inject

internal class GetAskedNotificationsPermissionUseCaseImpl @Inject constructor(
    private val notificationsSettingsRepository: NotificationsSettingsRepository,
) : GetAskedNotificationsPermissionUseCase {

    override fun invoke(): Boolean = notificationsSettingsRepository.askedNotificationsPermission().value
}
