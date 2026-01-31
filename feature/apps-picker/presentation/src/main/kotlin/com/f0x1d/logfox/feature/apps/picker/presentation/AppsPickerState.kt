package com.f0x1d.logfox.feature.apps.picker.presentation

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp

@Immutable
internal data class AppsPickerState(
    val topBarTitle: String,
    val apps: List<InstalledApp>,
    val checkedAppPackageNames: Set<String>,
    val searchedApps: List<InstalledApp>,
    val multiplySelectionEnabled: Boolean,
    val isLoading: Boolean,
    val searchActive: Boolean,
    val query: String,
)
