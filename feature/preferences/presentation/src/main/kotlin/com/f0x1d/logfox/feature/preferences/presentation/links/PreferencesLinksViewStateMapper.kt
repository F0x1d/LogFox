package com.f0x1d.logfox.feature.preferences.presentation.links

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class PreferencesLinksViewStateMapper @Inject constructor() :
    ViewStateMapper<PreferencesLinksState, PreferencesLinksViewState> {

    override fun map(state: PreferencesLinksState) = PreferencesLinksViewState
}
