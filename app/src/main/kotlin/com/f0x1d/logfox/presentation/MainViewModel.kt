package com.f0x1d.logfox.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetOpenCrashesOnStartupUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.notifications.GetAskedNotificationsPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    reducer: MainReducer,
    effectHandler: MainEffectHandler,
    viewStateMapper: MainViewStateMapper,
    getAskedNotificationsPermissionUseCase: GetAskedNotificationsPermissionUseCase,
    getOpenCrashesOnStartupUseCase: GetOpenCrashesOnStartupUseCase,
) : BaseStoreViewModel<MainViewState, MainState, MainCommand, MainSideEffect>(
    initialState = MainState(
        askedNotificationsPermission = getAskedNotificationsPermissionUseCase(),
        openCrashesOnStartup = getOpenCrashesOnStartupUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(MainSideEffect.StartLoggingServiceIfNeeded),
)
