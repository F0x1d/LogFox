package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

internal data class AppsPickerViewState(
    val topBarTitle: String,
    val apps: ImmutableList<InstalledApp>,
    val checkedAppPackageNames: ImmutableSet<String>,
    val searchedApps: ImmutableList<InstalledApp>,
    val multiplySelectionEnabled: Boolean,
    val isLoading: Boolean,
    val searchActive: Boolean,
    val query: String,
)
