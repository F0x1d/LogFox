package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.feature.apps.picker.InstalledApp

internal sealed interface AppsPickerCommand {
    data object BackPressed : AppsPickerCommand
    data class SearchActiveChanged(val active: Boolean) : AppsPickerCommand
    data class QueryChanged(val query: String) : AppsPickerCommand
    data class AppsLoaded(val apps: List<InstalledApp>) : AppsPickerCommand
    data class SearchedAppsUpdated(val apps: List<InstalledApp>) : AppsPickerCommand
    data class AppClicked(val app: InstalledApp) : AppsPickerCommand
}
