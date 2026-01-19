package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.preferences.CrashesSort

sealed interface CrashesSideEffect {
    // Business logic side effects
    data object LoadCrashes : CrashesSideEffect

    data class UpdateSortPreferences(val sortType: CrashesSort, val sortInReversedOrder: Boolean) : CrashesSideEffect

    data class DeleteCrashesByPackageName(val appCrash: AppCrash) : CrashesSideEffect

    data class DeleteCrash(val appCrash: AppCrash) : CrashesSideEffect

    data object ClearAllCrashes : CrashesSideEffect

    data class CheckAppDisabled(val packageName: String, val disabled: Boolean?) : CrashesSideEffect
}
