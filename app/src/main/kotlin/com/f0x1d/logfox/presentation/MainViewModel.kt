package com.f0x1d.logfox.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.NotificationsSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel
    @Inject
    constructor(
        reducer: MainReducer,
        effectHandler: MainEffectHandler,
        private val notificationsSettingsRepository: NotificationsSettingsRepository,
        private val crashesSettingsRepository: CrashesSettingsRepository,
    ) : BaseStoreViewModel<MainState, MainCommand, MainSideEffect>(
            initialState = MainState,
            reducer = reducer,
            effectHandlers = listOf(effectHandler),
            initialSideEffect = MainSideEffect.StartLoggingServiceIfNeeded,
        ) {
        var askedNotificationsPermission
            get() = notificationsSettingsRepository.askedNotificationsPermission
            set(value) {
                notificationsSettingsRepository.askedNotificationsPermission = value
            }

        val openCrashesOnStartup get() = crashesSettingsRepository.openCrashesOnStartup
    }
