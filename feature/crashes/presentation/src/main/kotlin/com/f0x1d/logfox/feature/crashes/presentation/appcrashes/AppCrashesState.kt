package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount

data class AppCrashesState(
    val packageName: String,
    val appName: String?,
    val crashes: List<AppCrashesCount> = emptyList(),
)
