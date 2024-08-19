package com.f0x1d.logfox.feature.logging.core.di

import com.f0x1d.logfox.feature.logging.core.store.LoggingStore
import com.f0x1d.logfox.feature.logging.core.store.LoggingStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface StoresModule {

    @Binds
    fun bindLoggingStore(
        loggingStoreImpl: LoggingStoreImpl,
    ): LoggingStore
}
