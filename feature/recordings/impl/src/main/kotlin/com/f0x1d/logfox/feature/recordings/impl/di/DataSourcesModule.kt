package com.f0x1d.logfox.feature.recordings.impl.di

import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSourceImpl
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingNotificationsLocalDataSource
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingNotificationsLocalDataSourceImpl
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
    fun bindRecordingLocalDataSource(impl: RecordingLocalDataSourceImpl): RecordingLocalDataSource

    @Binds
    fun bindRecordingNotificationsLocalDataSource(
        impl: RecordingNotificationsLocalDataSourceImpl,
    ): RecordingNotificationsLocalDataSource
}
