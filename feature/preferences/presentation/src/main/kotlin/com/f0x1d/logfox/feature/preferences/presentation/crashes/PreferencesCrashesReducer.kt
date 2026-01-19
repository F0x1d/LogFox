package com.f0x1d.logfox.feature.preferences.presentation.crashes

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import javax.inject.Inject

internal class PreferencesCrashesReducer @Inject constructor() :
    Reducer<PreferencesCrashesState, PreferencesCrashesCommand, PreferencesCrashesSideEffect> {

    override fun reduce(
        state: PreferencesCrashesState,
        command: PreferencesCrashesCommand,
    ): ReduceResult<PreferencesCrashesState, PreferencesCrashesSideEffect> = state.noSideEffects()
}
