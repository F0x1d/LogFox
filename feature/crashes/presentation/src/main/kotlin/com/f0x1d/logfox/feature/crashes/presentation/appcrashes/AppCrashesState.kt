package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.database.model.AppCrashesCount

data class AppCrashesState(val crashes: List<AppCrashesCount> = emptyList())
