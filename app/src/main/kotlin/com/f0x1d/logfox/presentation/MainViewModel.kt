package com.f0x1d.logfox.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.domain.GetAskedNotificationsPermissionUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetOpenCrashesOnStartupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    reducer: MainReducer,
    effectHandler: MainEffectHandler,
    getAskedNotificationsPermissionUseCase: GetAskedNotificationsPermissionUseCase,
    getOpenCrashesOnStartupUseCase: GetOpenCrashesOnStartupUseCase,
) : BaseStoreViewModel<MainState, MainCommand, MainSideEffect>(
    initialState = MainState(
        askedNotificationsPermission = getAskedNotificationsPermissionUseCase(),
        openCrashesOnStartup = getOpenCrashesOnStartupUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(MainSideEffect.StartLoggingServiceIfNeeded),
)
