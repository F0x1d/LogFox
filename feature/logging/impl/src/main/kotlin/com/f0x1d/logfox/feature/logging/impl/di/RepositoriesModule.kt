package com.f0x1d.logfox.feature.logging.impl.di

import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.feature.logging.impl.data.LoggingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun bindLoggingRepository(loggingRepositoryImpl: LoggingRepositoryImpl): LoggingRepository
}
