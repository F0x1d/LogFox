package com.f0x1d.logfox.feature.crashes.list.presentation

import com.f0x1d.logfox.database.entity.AppCrashesCount
import com.f0x1d.logfox.preferences.shared.crashes.CrashesSort

data class CrashesState(
    val crashes: List<AppCrashesCount> = emptyList(),
    val searchedCrashes: List<AppCrashesCount> = emptyList(),
    val currentSort: CrashesSort = CrashesSort.NEW,
    val sortInReversedOrder: Boolean = false,
    val query: String? = null,
)
