package com.f0x1d.logfox.feature.preferences.domain

interface SetAskedNotificationsPermissionUseCase {
    operator fun invoke(value: Boolean)
}
