package com.f0x1d.logfox.feature.recordings.core.di

import com.f0x1d.logfox.feature.recordings.core.controller.RecordingController
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingControllerImpl
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingNotificationController
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingNotificationControllerImpl
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
