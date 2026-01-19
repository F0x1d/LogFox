package com.f0x1d.logfox.presentation

import android.content.Context
import android.content.Intent
import com.f0x1d.logfox.core.compat.startForegroundServiceAvailable
import com.f0x1d.logfox.core.context.hasPermissionToReadLogs
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.logging.service.presentation.LoggingService
import com.f0x1d.logfox.feature.preferences.domain.notifications.SetAskedNotificationsPermissionUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class MainEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setAskedNotificationsPermissionUseCase: SetAskedNotificationsPermissionUseCase,
) : EffectHandler<MainSideEffect, MainCommand> {

    override suspend fun handle(effect: MainSideEffect, onCommand: suspend (MainCommand) -> Unit) {
        when (effect) {
            MainSideEffect.StartLoggingServiceIfNeeded -> {
                if (context.hasPermissionToReadLogs) {
                    Intent(context, LoggingService::class.java).let {
                        if (startForegroundServiceAvailable) {
                            context.startForegroundService(it)
                        } else {
                            context.startService(it)
                        }
                    }
                } else {
                    onCommand(MainCommand.ShowSetup)
                }
            }

            MainSideEffect.SaveNotificationsPermissionAsked -> {
                setAskedNotificationsPermissionUseCase(true)
            }

            MainSideEffect.OpenSetup -> Unit // Handled by Activity
        }
    }
}
