package com.f0x1d.logfox.feature.recordings.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object RecordingViewModelModule {

    @Provides
    @ViewModelScoped
    @RecordingId
    fun provideRecordingId(savedStateHandle: SavedStateHandle) = savedStateHandle.get<Long>("recording_id")!!
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RecordingId
