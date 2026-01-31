package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AppsPickerViewModel @Inject constructor(
    reducer: AppsPickerReducer,
    effectHandler: AppsPickerEffectHandler,
) : BaseStoreViewModel<AppsPickerState, AppsPickerState, AppsPickerCommand, AppsPickerSideEffect>(
    initialState = AppsPickerState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
    initialSideEffects = listOf(AppsPickerSideEffect.LoadApps),
)
