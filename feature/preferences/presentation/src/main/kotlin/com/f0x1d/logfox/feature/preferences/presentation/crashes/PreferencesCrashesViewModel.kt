package com.f0x1d.logfox.feature.preferences.presentation.crashes

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesCrashesViewModel @Inject constructor(reducer: PreferencesCrashesReducer) :
    BaseStoreViewModel<PreferencesCrashesState, PreferencesCrashesState, PreferencesCrashesCommand, PreferencesCrashesSideEffect>(
        initialState = PreferencesCrashesState,
        reducer = reducer,
        effectHandlers = emptyList(),
        viewStateMapper = ViewStateMapper.identity(),
    )
