package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.NotificationsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetAskedNotificationsPermissionUseCase
import javax.inject.Inject

internal class GetAskedNotificationsPermissionUseCaseImpl @Inject constructor(
    private val notificationsSettingsRepository: NotificationsSettingsRepository,
) : GetAskedNotificationsPermissionUseCase {

    override fun invoke(): Boolean = notificationsSettingsRepository.askedNotificationsPermission().value
}
