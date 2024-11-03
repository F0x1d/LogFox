package com.f0x1d.logfox.feature.crashes.details.presentation

import com.f0x1d.logfox.database.entity.AppCrash

data class CrashDetailsState(
    val crash: AppCrash? = null,
    val crashLog: String? = null,
    val blacklisted: Boolean? = null,
)
