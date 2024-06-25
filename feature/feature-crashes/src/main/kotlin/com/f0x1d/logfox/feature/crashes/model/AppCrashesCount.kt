package com.f0x1d.logfox.feature.crashes.model

import com.f0x1d.logfox.database.entity.AppCrash

data class AppCrashesCount(
    val lastCrash: AppCrash,
    val count: Int = 1,
)
