package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.database.model.AppCrash

sealed interface AppCrashesSideEffect {
    // Business logic side effects
    data object LoadCrashes : AppCrashesSideEffect

    data class DeleteCrash(val appCrash: AppCrash) : AppCrashesSideEffect

    // UI side effects
    data class NavigateToCrashDetails(val crashId: Long) : AppCrashesSideEffect
}
