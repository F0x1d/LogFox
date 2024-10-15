package com.f0x1d.logfox.feature.crashes.impl.di

import com.f0x1d.logfox.feature.crashes.api.controller.CrashesController
import com.f0x1d.logfox.feature.crashes.api.controller.CrashesNotificationsController
import com.f0x1d.logfox.feature.crashes.impl.controller.CrashesControllerImpl
import com.f0x1d.logfox.feature.crashes.impl.controller.CrashesNotificationsControllerImpl
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
