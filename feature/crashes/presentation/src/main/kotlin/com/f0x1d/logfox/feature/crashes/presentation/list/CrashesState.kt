package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.database.model.AppCrashesCount
import com.f0x1d.logfox.feature.preferences.CrashesSort

data class CrashesState(
    val crashes: List<AppCrashesCount> = emptyList(),
    val searchedCrashes: List<AppCrashesCount> = emptyList(),
    val currentSort: CrashesSort = CrashesSort.NEW,
    val sortInReversedOrder: Boolean = false,
    val query: String? = null,
)
