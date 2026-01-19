package com.f0x1d.logfox.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.domain.GetAskedNotificationsPermissionUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetOpenCrashesOnStartupUseCase
import com.f0x1d.logfox.feature.preferences.domain.SetAskedNotificationsPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    reducer: MainReducer,
    effectHandler: MainEffectHandler,
    private val getAskedNotificationsPermissionUseCase: GetAskedNotificationsPermissionUseCase,
    private val setAskedNotificationsPermissionUseCase: SetAskedNotificationsPermissionUseCase,
    private val getOpenCrashesOnStartupUseCase: GetOpenCrashesOnStartupUseCase,
) : BaseStoreViewModel<MainState, MainCommand, MainSideEffect>(
    initialState = MainState,
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffect = MainSideEffect.StartLoggingServiceIfNeeded,
) {

    var askedNotificationsPermission
        get() = getAskedNotificationsPermissionUseCase()
        set(value) {
            setAskedNotificationsPermissionUseCase(value)
        }

    val openCrashesOnStartup get() = getOpenCrashesOnStartupUseCase()
}
