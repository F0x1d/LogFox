package com.f0x1d.logfox.feature.logging.core.di

import com.f0x1d.logfox.feature.logging.core.repository.LoggingRepository
import com.f0x1d.logfox.feature.logging.core.repository.LoggingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun bindLoggingRepository(
        loggingRepositoryImpl: LoggingRepositoryImpl,
    ): LoggingRepository
}
