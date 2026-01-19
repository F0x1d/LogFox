package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AppsPickerViewModel @Inject constructor(
    reducer: AppsPickerReducer,
    effectHandler: AppsPickerEffectHandler,
) : BaseStoreViewModel<AppsPickerState, AppsPickerCommand, AppsPickerSideEffect>(
    initialState = AppsPickerState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffect = AppsPickerSideEffect.LoadApps,
)
