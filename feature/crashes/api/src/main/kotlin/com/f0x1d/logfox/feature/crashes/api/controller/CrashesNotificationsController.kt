package com.f0x1d.logfox.feature.crashes.api.controller

import com.f0x1d.logfox.arch.CRASHES_CHANNEL_GROUP_ID
import com.f0x1d.logfox.database.entity.AppCrash

interface CrashesNotificationsController {
    fun sendErrorNotification(appCrash: AppCrash, crashLog: String?)
    fun cancelCrashNotificationFor(appCrash: AppCrash)
    fun cancelAllCrashNotifications()
}

val AppCrash.notificationChannelId get() = "${CRASHES_CHANNEL_GROUP_ID}_${crashType.readableName}_$packageName"
