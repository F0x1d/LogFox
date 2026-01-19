package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.feature.database.model.AppCrash

data class CrashDetailsState(
    val crash: AppCrash? = null,
    val crashLog: String? = null,
    val blacklisted: Boolean? = null,
)
