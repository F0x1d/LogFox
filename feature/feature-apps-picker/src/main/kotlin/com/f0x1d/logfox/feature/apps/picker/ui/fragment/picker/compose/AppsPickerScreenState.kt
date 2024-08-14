package com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker.compose

import com.f0x1d.logfox.model.InstalledApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AppsPickerScreenState(
    val apps: ImmutableList<InstalledApp> = persistentListOf(),
    val searchedApps: ImmutableList<InstalledApp> = persistentListOf(),
    val isLoading: Boolean = true,
    val searchActive: Boolean = false,
    val query: String = "",
)

data class AppsPickerScreenListener(
    val onBackClicked: () -> Unit,
    val onAppClicked: (InstalledApp) -> Unit,
    val onSearchActiveChanged: (Boolean) -> Unit,
    val onQueryChanged: (String) -> Unit,
)

internal val MockAppsPickerScreenListener = AppsPickerScreenListener(
    onBackClicked = { },
    onAppClicked = { },
    onSearchActiveChanged = { },
    onQueryChanged = { },
)
