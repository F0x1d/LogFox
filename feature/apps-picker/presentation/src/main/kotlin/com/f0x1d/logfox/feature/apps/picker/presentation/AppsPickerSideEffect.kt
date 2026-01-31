package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp

internal sealed interface AppsPickerSideEffect {
    // Business logic side effects
    data object LoadApps : AppsPickerSideEffect
    data class FilterApps(val query: String, val apps: List<InstalledApp>) : AppsPickerSideEffect

    // UI side effects
    data object PopBackStack : AppsPickerSideEffect
    data class HandleAppSelection(val app: InstalledApp) : AppsPickerSideEffect
}
