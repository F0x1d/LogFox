package com.f0x1d.logfox.feature.preferences.presentation.crashes

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class PreferencesCrashesViewStateMapper @Inject constructor() :
    ViewStateMapper<PreferencesCrashesState, PreferencesCrashesViewState> {

    override fun map(state: PreferencesCrashesState) = PreferencesCrashesViewState
}
