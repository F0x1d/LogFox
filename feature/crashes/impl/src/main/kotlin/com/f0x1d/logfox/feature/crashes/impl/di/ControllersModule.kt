package com.f0x1d.logfox.feature.crashes.impl.di

import com.f0x1d.logfox.feature.crashes.api.data.CrashesController
import com.f0x1d.logfox.feature.crashes.api.data.CrashesNotificationsController
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesControllerImpl
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesNotificationsControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ControllersModule {

    @Binds
    fun bindCrashesController(
        crashesControllerImpl: CrashesControllerImpl,
    ): CrashesController

    @Binds
    fun bindCrashesNotificationsController(
        crashesNotificationsControllerImpl: CrashesNotificationsControllerImpl,
    ): CrashesNotificationsController
}
