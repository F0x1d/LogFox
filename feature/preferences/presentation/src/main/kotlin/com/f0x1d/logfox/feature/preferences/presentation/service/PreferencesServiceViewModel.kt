package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesServiceViewModel @Inject constructor(
    reducer: PreferencesServiceReducer,
    effectHandler: PreferencesServiceEffectHandler,
) : BaseStoreViewModel<PreferencesServiceState, PreferencesServiceCommand, PreferencesServiceSideEffect>(
    initialState = PreferencesServiceState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(PreferencesServiceSideEffect.LoadPreferences),
)
