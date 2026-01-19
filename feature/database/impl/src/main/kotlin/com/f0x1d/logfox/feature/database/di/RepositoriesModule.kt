package com.f0x1d.logfox.feature.database.di

import com.f0x1d.logfox.feature.database.data.AppCrashRepository
import com.f0x1d.logfox.feature.database.data.AppCrashRepositoryImpl
import com.f0x1d.logfox.feature.database.data.DisabledAppRepository
import com.f0x1d.logfox.feature.database.data.DisabledAppRepositoryImpl
import com.f0x1d.logfox.feature.database.data.LogRecordingRepository
import com.f0x1d.logfox.feature.database.data.LogRecordingRepositoryImpl
import com.f0x1d.logfox.feature.database.data.UserFilterRepository
import com.f0x1d.logfox.feature.database.data.UserFilterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun bindAppCrashRepository(impl: AppCrashRepositoryImpl): AppCrashRepository

    @Binds
    fun bindLogRecordingRepository(impl: LogRecordingRepositoryImpl): LogRecordingRepository

    @Binds
    fun bindUserFilterRepository(impl: UserFilterRepositoryImpl): UserFilterRepository

    @Binds
    fun bindDisabledAppRepository(impl: DisabledAppRepositoryImpl): DisabledAppRepository
}
