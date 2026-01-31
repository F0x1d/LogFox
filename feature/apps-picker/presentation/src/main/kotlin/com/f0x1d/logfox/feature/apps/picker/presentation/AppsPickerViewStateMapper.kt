package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class AppsPickerViewStateMapper @Inject constructor() : ViewStateMapper<AppsPickerState, AppsPickerViewState> {
    override fun map(state: AppsPickerState) = AppsPickerViewState(
        topBarTitle = state.topBarTitle,
        apps = state.apps,
        checkedAppPackageNames = state.checkedAppPackageNames,
        searchedApps = state.searchedApps,
        multiplySelectionEnabled = state.multiplySelectionEnabled,
        isLoading = state.isLoading,
        searchActive = state.searchActive,
        query = state.query,
    )
}
