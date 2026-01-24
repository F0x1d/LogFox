package com.f0x1d.logfox.feature.preferences.presentation.notifications

import android.content.Context
import com.f0x1d.logfox.core.context.hasNotificationsPermission
import com.f0x1d.logfox.core.tea.EffectHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class PreferencesNotificationsEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) : EffectHandler<PreferencesNotificationsSideEffect, PreferencesNotificationsCommand> {

    override suspend fun handle(
        effect: PreferencesNotificationsSideEffect,
        onCommand: suspend (PreferencesNotificationsCommand) -> Unit,
    ) {
        when (effect) {
            is PreferencesNotificationsSideEffect.CheckPermission -> {
                onCommand(
                    PreferencesNotificationsCommand.PermissionChecked(
                        hasPermission = context.hasNotificationsPermission(),
                    ),
                )
            }

            // UI side effects - handled by Fragment
            is PreferencesNotificationsSideEffect.OpenLoggingChannelSettings -> Unit
            is PreferencesNotificationsSideEffect.OpenAppNotificationSettings -> Unit
        }
    }
}
