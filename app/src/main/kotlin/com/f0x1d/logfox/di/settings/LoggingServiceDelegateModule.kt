package com.f0x1d.logfox.di.settings

import android.content.Context
import com.f0x1d.feature.logging.service.LoggingService
import com.f0x1d.logfox.context.sendService
import com.f0x1d.logfox.feature.settings.LoggingServiceDelegate
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
internal interface LoggingServiceDelegateModule {

    @Binds
    fun bindLoggingServiceDelegate(
        loggingServiceDelegateImpl: LoggingServiceDelegateImpl,
    ): LoggingServiceDelegate
}

internal class LoggingServiceDelegateImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LoggingServiceDelegate {
    override fun restartLogging() {
        context.sendService<LoggingService>(action = LoggingService.ACTION_RESTART_LOGGING)
    }
}
