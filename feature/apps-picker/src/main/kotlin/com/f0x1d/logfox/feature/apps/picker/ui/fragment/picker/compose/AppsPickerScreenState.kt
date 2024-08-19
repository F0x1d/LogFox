package com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker.compose

import com.f0x1d.logfox.model.InstalledApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class AppsPickerScreenState(
    val topBarTitle: String = "Apps",
    val apps: ImmutableList<InstalledApp> = persistentListOf(),
    val checkedAppPackageNames: ImmutableSet<String> = persistentSetOf(),
    val searchedApps: ImmutableList<InstalledApp> = persistentListOf(),
    val multiplySelectionEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val searchActive: Boolean = false,
    val query: String = "",
)

data class AppsPickerScreenListener(
    val onBackClicked: () -> Unit,
    val onAppClicked: (InstalledApp) -> Unit,
    val onAppChecked: (InstalledApp, Boolean) -> Unit,
    val onSearchActiveChanged: (Boolean) -> Unit,
    val onQueryChanged: (String) -> Unit,
)

internal val MockAppsPickerScreenListener = AppsPickerScreenListener(
    onBackClicked = { },
    onAppClicked = { },
    onAppChecked = { _, _ -> },
    onSearchActiveChanged = { },
    onQueryChanged = { },
)
