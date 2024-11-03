package com.f0x1d.logfox.feature.logging.service.di

import com.f0x1d.logfox.feature.logging.api.presentation.LoggingServiceDelegate
import com.f0x1d.logfox.feature.logging.service.presentation.LoggingServiceDelegateImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface LoggingServiceDelegateModule {

    @Binds
    fun bindLoggingServiceDelegate(
        loggingServiceDelegateImpl: LoggingServiceDelegateImpl,
    ): LoggingServiceDelegate
}
