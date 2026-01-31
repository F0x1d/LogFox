package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesServiceViewModel @Inject constructor(
    reducer: PreferencesServiceReducer,
    effectHandler: PreferencesServiceEffectHandler,
    viewStateMapper: PreferencesServiceViewStateMapper,
) : BaseStoreViewModel<PreferencesServiceViewState, PreferencesServiceState, PreferencesServiceCommand, PreferencesServiceSideEffect>(
    initialState = PreferencesServiceState(
        selectedTerminalType = TerminalType.Default,
        terminalNames = emptyList(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(PreferencesServiceSideEffect.LoadPreferences),
)
