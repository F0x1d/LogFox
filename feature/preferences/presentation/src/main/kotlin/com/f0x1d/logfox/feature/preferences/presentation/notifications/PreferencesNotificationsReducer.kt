package com.f0x1d.logfox.feature.preferences.presentation.notifications

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class PreferencesNotificationsReducer @Inject constructor() : Reducer<PreferencesNotificationsState, PreferencesNotificationsCommand, PreferencesNotificationsSideEffect> {

    override fun reduce(
        state: PreferencesNotificationsState,
        command: PreferencesNotificationsCommand,
    ): ReduceResult<PreferencesNotificationsState, PreferencesNotificationsSideEffect> = when (command) {
        is PreferencesNotificationsCommand.CheckPermission -> {
            state.withSideEffects(PreferencesNotificationsSideEffect.CheckPermission)
        }

        is PreferencesNotificationsCommand.PermissionChecked -> {
            state.copy(hasNotificationsPermission = command.hasPermission).noSideEffects()
        }

        is PreferencesNotificationsCommand.OpenLoggingNotificationSettings -> {
            state.withSideEffects(PreferencesNotificationsSideEffect.OpenLoggingChannelSettings)
        }

        is PreferencesNotificationsCommand.OpenNotificationsPermissionSettings -> {
            state.withSideEffects(
                PreferencesNotificationsSideEffect.OpenAppNotificationSettings,
            )
        }
    }
}
