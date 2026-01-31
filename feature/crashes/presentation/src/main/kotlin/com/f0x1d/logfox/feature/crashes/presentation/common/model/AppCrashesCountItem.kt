package com.f0x1d.logfox.feature.crashes.presentation.common.model

import com.f0x1d.logfox.feature.crashes.api.model.CrashType

data class AppCrashesCountItem(
    val lastCrashId: Long,
    val appName: String?,
    val packageName: String,
    val crashType: CrashType,
    val count: Int,
    val formattedDate: String,
)
