package com.f0x1d.logfox.feature.logging.service.presentation

import android.content.Context
import com.f0x1d.logfox.core.context.sendService
import com.f0x1d.logfox.feature.logging.api.presentation.LoggingServiceDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class LoggingServiceDelegateImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LoggingServiceDelegate {
    override fun clearLogs() {
        context.sendService<LoggingService>(LoggingService.ACTION_CLEAR_LOGS)
    }

    override fun restartLogging() {
        context.sendService<LoggingService>(LoggingService.ACTION_RESTART_LOGGING)
    }

    override fun killService() {
        context.sendService<LoggingService>(LoggingService.ACTION_KILL_SERVICE)
    }
}
