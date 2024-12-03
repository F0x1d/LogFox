package com.f0x1d.logfox.feature.logging.list.di

import android.content.Intent
import android.net.Uri
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
    @FileUri
    fun provideDeepLinkIntent(savedStateHandle: SavedStateHandle) = savedStateHandle.get<Intent>(
        NavController.KEY_DEEP_LINK_INTENT
    )?.data ?: savedStateHandle.get<Uri>("file_uri")
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class FileUri
