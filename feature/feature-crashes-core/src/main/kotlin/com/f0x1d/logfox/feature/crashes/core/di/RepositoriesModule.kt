package com.f0x1d.logfox.feature.crashes.core.di

import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepositoryImpl
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
}
