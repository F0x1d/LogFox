package com.f0x1d.logfox.feature.crashes.apps.list.presentation

import com.f0x1d.logfox.database.entity.AppCrashesCount

data class AppCrashesState(
    val crashes: List<AppCrashesCount> = emptyList(),
)
