package com.f0x1d.logfox.feature.preferences.presentation.notifications

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class PreferencesNotificationsViewStateMapper @Inject constructor() :
    ViewStateMapper<PreferencesNotificationsState, PreferencesNotificationsViewState> {

    override fun map(state: PreferencesNotificationsState) = PreferencesNotificationsViewState(
        hasNotificationsPermission = state.hasNotificationsPermission,
        notificationsChannelsAvailable = state.notificationsChannelsAvailable,
    )
}
