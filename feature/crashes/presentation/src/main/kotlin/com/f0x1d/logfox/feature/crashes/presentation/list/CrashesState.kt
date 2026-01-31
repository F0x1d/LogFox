package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount
import com.f0x1d.logfox.feature.preferences.api.CrashesSort

internal data class CrashesState(
    val crashes: List<AppCrashesCount>,
    val searchedCrashes: List<AppCrashesCount>,
    val currentSort: CrashesSort,
    val sortInReversedOrder: Boolean,
    val query: String?,
)
