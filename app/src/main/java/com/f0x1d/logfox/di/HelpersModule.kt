package com.f0x1d.logfox.di

import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.repository.logging.base.LoggingHelperRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelpersModule {

    @Singleton
    @Provides
    fun provideHelpers(
        crashesRepository: CrashesRepository,
        recordingsRepository: RecordingsRepository,
        filtersRepository: FiltersRepository
    ): Array<LoggingHelperRepository> = arrayOf(
        crashesRepository,
        recordingsRepository,
        filtersRepository
    )
}