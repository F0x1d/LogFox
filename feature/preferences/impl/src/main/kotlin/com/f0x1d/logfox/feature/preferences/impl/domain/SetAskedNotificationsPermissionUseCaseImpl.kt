package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.NotificationsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.SetAskedNotificationsPermissionUseCase
import javax.inject.Inject

internal class SetAskedNotificationsPermissionUseCaseImpl @Inject constructor(
    private val notificationsSettingsRepository: NotificationsSettingsRepository,
) : SetAskedNotificationsPermissionUseCase {

    override fun invoke(value: Boolean) {
        notificationsSettingsRepository.askedNotificationsPermission().set(value)
    }
}
