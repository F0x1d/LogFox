package com.f0x1d.logfox.feature.crashes.details.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object CrashDetailsViewModelModule {

    @Provides
    @ViewModelScoped
    @CrashId
    fun provideCrashId(savedStateHandle: SavedStateHandle) = savedStateHandle.get<Long>("crash_id")!!
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CrashId
