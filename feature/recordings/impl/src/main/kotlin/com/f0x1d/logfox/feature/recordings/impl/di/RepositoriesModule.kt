package com.f0x1d.logfox.feature.recordings.impl.di

import com.f0x1d.logfox.feature.recordings.api.repository.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.impl.repository.RecordingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun bindRecordingsRepository(
        recordingsRepositoryImpl: RecordingsRepositoryImpl,
    ): RecordingsRepository
}
