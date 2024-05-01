package com.f0x1d.logfox.di.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object EditFilterEditViewModelModule {

    @Provides
    @ViewModelScoped
    @FilterId
    fun provideFilterId(savedStateHandle: SavedStateHandle) = savedStateHandle.get<Long>("filter_id")
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FilterId