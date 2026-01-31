package com.f0x1d.logfox.feature.apps.picker.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import javax.inject.Inject

@HiltViewModel
internal class AppsPickerViewModel @Inject constructor(
    reducer: AppsPickerReducer,
    effectHandler: AppsPickerEffectHandler,
    viewStateMapper: AppsPickerViewStateMapper,
) : BaseStoreViewModel<AppsPickerViewState, AppsPickerState, AppsPickerCommand, AppsPickerSideEffect>(
    initialState = AppsPickerState(
        topBarTitle = "Apps",
        apps = persistentListOf(),
        checkedAppPackageNames = persistentSetOf(),
        searchedApps = persistentListOf(),
        multiplySelectionEnabled = true,
        isLoading = true,
        searchActive = false,
        query = "",
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(AppsPickerSideEffect.LoadApps),
)
