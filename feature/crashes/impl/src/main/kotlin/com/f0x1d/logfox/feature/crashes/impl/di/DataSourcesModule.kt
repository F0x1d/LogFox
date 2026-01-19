package com.f0x1d.logfox.feature.crashes.impl.di

import com.f0x1d.logfox.feature.crashes.impl.data.CrashesLocalDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesLocalDataSourceImpl
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesNotificationsLocalDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesNotificationsLocalDataSourceImpl
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesSearchLocalDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesSearchLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourcesModule {

    @Binds
    @Singleton
    fun bindCrashesSearchLocalDataSource(
        impl: CrashesSearchLocalDataSourceImpl,
    ): CrashesSearchLocalDataSource

    @Binds
    fun bindCrashesLocalDataSource(
        impl: CrashesLocalDataSourceImpl,
    ): CrashesLocalDataSource

    @Binds
    fun bindCrashesNotificationsLocalDataSource(
        impl: CrashesNotificationsLocalDataSourceImpl,
    ): CrashesNotificationsLocalDataSource
}
