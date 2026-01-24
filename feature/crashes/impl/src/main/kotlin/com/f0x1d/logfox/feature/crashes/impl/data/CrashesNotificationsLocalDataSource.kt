package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

internal interface CrashesNotificationsLocalDataSource {
    fun sendErrorNotification(appCrash: AppCrash, crashLog: String?)
    fun cancelCrashNotificationFor(appCrash: AppCrash)
    fun cancelAllCrashNotifications()
}
