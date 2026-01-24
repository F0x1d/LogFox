package com.f0x1d.logfox.feature.database.di

import com.f0x1d.logfox.feature.database.data.AppCrashDataSource
import com.f0x1d.logfox.feature.database.data.AppCrashDataSourceImpl
import com.f0x1d.logfox.feature.database.data.DisabledAppDataSource
import com.f0x1d.logfox.feature.database.data.DisabledAppDataSourceImpl
import com.f0x1d.logfox.feature.database.data.LogRecordingDataSource
import com.f0x1d.logfox.feature.database.data.LogRecordingDataSourceImpl
import com.f0x1d.logfox.feature.database.data.UserFilterDataSource
import com.f0x1d.logfox.feature.database.data.UserFilterDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourcesModule {

    @Binds
    fun bindUserFilterDataSource(impl: UserFilterDataSourceImpl): UserFilterDataSource

    @Binds
    fun bindLogRecordingDataSource(impl: LogRecordingDataSourceImpl): LogRecordingDataSource

    @Binds
    fun bindAppCrashDataSource(impl: AppCrashDataSourceImpl): AppCrashDataSource

    @Binds
    fun bindDisabledAppDataSource(impl: DisabledAppDataSourceImpl): DisabledAppDataSource
}
