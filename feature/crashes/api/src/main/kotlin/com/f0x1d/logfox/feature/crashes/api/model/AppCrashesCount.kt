package com.f0x1d.logfox.feature.crashes.api.model

data class AppCrashesCount(val lastCrash: AppCrash, val count: Int = 1)
