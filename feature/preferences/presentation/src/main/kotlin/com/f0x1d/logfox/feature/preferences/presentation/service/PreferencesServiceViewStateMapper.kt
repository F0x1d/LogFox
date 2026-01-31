package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class PreferencesServiceViewStateMapper @Inject constructor() :
    ViewStateMapper<PreferencesServiceState, PreferencesServiceViewState> {

    override fun map(state: PreferencesServiceState) = PreferencesServiceViewState(
        selectedTerminalType = state.selectedTerminalType,
        terminalNames = state.terminalNames,
    )
}
