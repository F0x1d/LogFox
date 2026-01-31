package com.f0x1d.logfox.feature.preferences.presentation.menu

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class PreferencesMenuViewStateMapper @Inject constructor() :
    ViewStateMapper<PreferencesMenuState, PreferencesMenuViewState> {

    override fun map(state: PreferencesMenuState) = PreferencesMenuViewState(
        versionName = state.versionName,
        versionCode = state.versionCode,
        isDebug = state.isDebug,
    )
}
