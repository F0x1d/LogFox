package com.f0x1d.logfox.feature.preferences.presentation.notifications

import com.f0x1d.logfox.core.compat.notificationsChannelsAvailable
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesNotificationsViewModel @Inject constructor(
    reducer: PreferencesNotificationsReducer,
    effectHandler: PreferencesNotificationsEffectHandler,
) : BaseStoreViewModel<PreferencesNotificationsState, PreferencesNotificationsCommand, PreferencesNotificationsSideEffect>(
    initialState = PreferencesNotificationsState(
        notificationsChannelsAvailable = notificationsChannelsAvailable,
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
)
