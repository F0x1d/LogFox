package com.f0x1d.logfox.feature.preferences.api.domain.notifications

interface SetAskedNotificationsPermissionUseCase {
    operator fun invoke(value: Boolean)
}
