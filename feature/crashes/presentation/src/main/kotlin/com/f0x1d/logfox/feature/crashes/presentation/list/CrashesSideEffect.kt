package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.feature.preferences.api.CrashesSort

internal sealed interface CrashesSideEffect {
    // Business logic side effects
    data object LoadCrashes : CrashesSideEffect

    data class UpdateSearchQuery(val query: String) : CrashesSideEffect

    data class UpdateSortPreferences(val sortType: CrashesSort, val sortInReversedOrder: Boolean) : CrashesSideEffect

    data class DeleteCrashesByPackageName(val packageName: String) : CrashesSideEffect

    data class DeleteCrash(val crashId: Long) : CrashesSideEffect

    data object ClearAllCrashes : CrashesSideEffect

    data class CheckAppDisabled(val packageName: String, val disabled: Boolean?) : CrashesSideEffect

    // UI side effects - handled by Fragment
    data class NavigateToCrashDetails(val crashId: Long) : CrashesSideEffect
    data class NavigateToAppCrashes(val packageName: String, val appName: String?) : CrashesSideEffect
    data object NavigateToBlacklist : CrashesSideEffect
}
