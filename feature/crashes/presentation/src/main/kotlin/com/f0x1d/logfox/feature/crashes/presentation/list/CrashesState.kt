package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem
import com.f0x1d.logfox.feature.preferences.CrashesSort

data class CrashesState(
    val crashes: List<AppCrashesCountItem> = emptyList(),
    val searchedCrashes: List<AppCrashesCountItem> = emptyList(),
    val currentSort: CrashesSort = CrashesSort.NEW,
    val sortInReversedOrder: Boolean = false,
    val query: String? = null,
)
