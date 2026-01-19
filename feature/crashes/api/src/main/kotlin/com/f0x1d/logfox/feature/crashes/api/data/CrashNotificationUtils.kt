package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.notifications.api.CRASHES_CHANNEL_GROUP_ID

val AppCrash.notificationChannelId get() = "${CRASHES_CHANNEL_GROUP_ID}_${crashType.readableName}_$packageName"
