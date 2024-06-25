package com.f0x1d.logfox.feature.crashes.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object AppCrashesViewModelModule {

    @Provides
    @ViewModelScoped
    @PackageName
    fun providePackageName(savedStateHandle: SavedStateHandle) = savedStateHandle.get<String>("package_name")!!

    @Provides
    @ViewModelScoped
    @AppName
    fun provideAppName(savedStateHandle: SavedStateHandle) = savedStateHandle.get<String>("app_name")
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PackageName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppName
