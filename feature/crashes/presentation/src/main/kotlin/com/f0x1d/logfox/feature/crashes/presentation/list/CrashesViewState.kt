package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem
import com.f0x1d.logfox.feature.preferences.CrashesSort

internal data class CrashesViewState(
    val crashes: List<AppCrashesCountItem>,
    val searchedCrashes: List<AppCrashesCountItem>,
    val currentSort: CrashesSort,
    val sortInReversedOrder: Boolean,
    val query: String?,
)
