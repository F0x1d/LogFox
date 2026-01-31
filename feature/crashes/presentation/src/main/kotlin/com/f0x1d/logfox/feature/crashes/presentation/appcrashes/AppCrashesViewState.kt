package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem

internal data class AppCrashesViewState(
    val packageName: String,
    val appName: String?,
    val crashes: List<AppCrashesCountItem>,
)
