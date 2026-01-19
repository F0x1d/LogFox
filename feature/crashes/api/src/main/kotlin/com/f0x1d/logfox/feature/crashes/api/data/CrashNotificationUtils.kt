package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.core.context.CRASHES_CHANNEL_GROUP_ID
import com.f0x1d.logfox.feature.database.model.AppCrash

val AppCrash.notificationChannelId get() = "${CRASHES_CHANNEL_GROUP_ID}_${crashType.readableName}_$packageName"
