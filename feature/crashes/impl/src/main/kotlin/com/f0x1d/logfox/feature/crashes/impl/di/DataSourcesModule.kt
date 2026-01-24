package com.f0x1d.logfox.feature.crashes.impl.di

import com.f0x1d.logfox.feature.crashes.impl.data.AppInfoDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.AppInfoDataSourceImpl
import com.f0x1d.logfox.feature.crashes.impl.data.CrashCollectorDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashCollectorDataSourceImpl
import com.f0x1d.logfox.feature.crashes.impl.data.CrashDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesNotificationsLocalDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesNotificationsLocalDataSourceImpl
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesSearchLocalDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesSearchLocalDataSourceImpl
import com.f0x1d.logfox.feature.crashes.impl.data.reader.ANRDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.reader.JNICrashDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.reader.JavaCrashDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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
    fun bindCrashesNotificationsLocalDataSource(
        impl: CrashesNotificationsLocalDataSourceImpl,
    ): CrashesNotificationsLocalDataSource

    @Binds
    fun bindAppInfoDataSource(
        impl: AppInfoDataSourceImpl,
    ): AppInfoDataSource

    @Binds
    @Singleton
    fun bindCrashCollectorDataSource(
        impl: CrashCollectorDataSourceImpl,
    ): CrashCollectorDataSource

    @Binds
    @IntoSet
    fun bindJavaCrashDataSource(
        impl: JavaCrashDataSource,
    ): CrashDataSource

    @Binds
    @IntoSet
    fun bindJNICrashDataSource(
        impl: JNICrashDataSource,
    ): CrashDataSource

    @Binds
    @IntoSet
    fun bindANRDataSource(
        impl: ANRDataSource,
    ): CrashDataSource
}
