package com.f0x1d.logfox.feature.crashes.impl.di

import com.f0x1d.logfox.feature.crashes.api.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.repository.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.impl.repository.CrashesRepositoryImpl
import com.f0x1d.logfox.feature.crashes.impl.repository.DisabledAppsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun bindCrashesRepository(
        crashesRepositoryImpl: CrashesRepositoryImpl,
    ): CrashesRepository

    @Binds
    fun provideDisabledAppsRepository(
        disabledAppsRepositoryImpl: DisabledAppsRepositoryImpl,
    ): DisabledAppsRepository
}
