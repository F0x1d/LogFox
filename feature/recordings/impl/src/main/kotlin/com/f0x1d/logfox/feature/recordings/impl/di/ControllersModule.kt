package com.f0x1d.logfox.feature.recordings.impl.di

import com.f0x1d.logfox.feature.recordings.api.data.RecordingController
import com.f0x1d.logfox.feature.recordings.api.data.RecordingNotificationController
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingControllerImpl
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingNotificationControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ControllersModule {

    @Binds
    fun bindRecordingController(
        recordingControllerImpl: RecordingControllerImpl,
    ): RecordingController

    @Binds
    fun bindRecordingNotificationController(
        recordingNotificationControllerImpl: RecordingNotificationControllerImpl,
    ): RecordingNotificationController
}
