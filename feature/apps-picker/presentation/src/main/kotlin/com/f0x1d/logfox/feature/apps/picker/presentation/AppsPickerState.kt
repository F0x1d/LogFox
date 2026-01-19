package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class AppsPickerState(
    val topBarTitle: String = "Apps",
    val apps: ImmutableList<InstalledApp> = persistentListOf(),
    val checkedAppPackageNames: ImmutableSet<String> = persistentSetOf(),
    val searchedApps: ImmutableList<InstalledApp> = persistentListOf(),
    val multiplySelectionEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val searchActive: Boolean = false,
    val query: String = "",
)
