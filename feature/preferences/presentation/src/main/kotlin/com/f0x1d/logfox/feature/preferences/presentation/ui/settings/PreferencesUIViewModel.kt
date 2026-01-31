package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesUIViewModel
@Inject
constructor(
    reducer: PreferencesUIReducer,
    effectHandler: PreferencesUIEffectHandler,
) : BaseStoreViewModel<PreferencesUIState, PreferencesUIState, PreferencesUICommand, PreferencesUISideEffect>(
    initialState = PreferencesUIState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
    initialSideEffects = listOf(PreferencesUISideEffect.LoadPreferences),
)
