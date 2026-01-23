package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.database.model.AppCrashesCount
import com.f0x1d.logfox.feature.preferences.CrashesSort

sealed interface CrashesCommand {
    data object Load : CrashesCommand

    data class CrashesLoaded(
        val crashes: List<AppCrashesCount>,
        val sortType: CrashesSort,
        val sortInReversedOrder: Boolean,
    ) : CrashesCommand

    data class SearchedCrashesLoaded(val searchedCrashes: List<AppCrashesCount>) : CrashesCommand

    data class UpdateQuery(val query: String) : CrashesCommand

    data class UpdateSort(val sortType: CrashesSort, val sortInReversedOrder: Boolean) : CrashesCommand

    data class DeleteCrashesByPackageName(val appCrash: AppCrash) : CrashesCommand

    data class DeleteCrash(val appCrash: AppCrash) : CrashesCommand

    data object ClearCrashes : CrashesCommand

    data class CheckAppDisabled(val packageName: String, val disabled: Boolean? = null) : CrashesCommand

    // Navigation commands
    data class CrashClicked(val crashId: Long, val count: Int, val packageName: String, val appName: String?) : CrashesCommand
    data class SearchedCrashClicked(val crashId: Long) : CrashesCommand
    data object OpenBlacklist : CrashesCommand
}
