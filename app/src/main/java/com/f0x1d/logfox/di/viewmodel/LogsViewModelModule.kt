package com.f0x1d.logfox.di.viewmodel

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object LogsViewModelModule {

    @Provides
    @ViewModelScoped
    @DeepLinkIntent
    fun provideDeepLinkIntent(savedStateHandle: SavedStateHandle) = savedStateHandle.get<Intent>(
        NavController.KEY_DEEP_LINK_INTENT
    )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeepLinkIntent