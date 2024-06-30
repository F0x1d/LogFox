package com.f0x1d.logfox.feature.crashes.core.di

import com.f0x1d.logfox.feature.crashes.core.controller.CrashesController
import com.f0x1d.logfox.feature.crashes.core.controller.CrashesControllerImpl
import com.f0x1d.logfox.feature.crashes.core.controller.CrashesNotificationsController
import com.f0x1d.logfox.feature.crashes.core.controller.CrashesNotificationsControllerImpl
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
