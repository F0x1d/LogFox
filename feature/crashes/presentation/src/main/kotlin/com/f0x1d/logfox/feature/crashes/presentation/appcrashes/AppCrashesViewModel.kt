package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.di.AppName
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.di.PackageName
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AppCrashesViewModel @Inject constructor(
    @PackageName packageName: String,
    @AppName appName: String?,
    reducer: AppCrashesReducer,
    effectHandler: AppCrashesEffectHandler,
    viewStateMapper: AppCrashesViewStateMapper,
) : BaseStoreViewModel<AppCrashesViewState, AppCrashesState, AppCrashesCommand, AppCrashesSideEffect>(
    initialState = AppCrashesState(
        packageName = packageName,
        appName = appName,
        crashes = emptyList(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(AppCrashesSideEffect.LoadCrashes),
)
