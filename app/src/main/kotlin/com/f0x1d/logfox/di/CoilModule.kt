package com.f0x1d.logfox.di

import android.content.Context
import coil.ImageLoader
import com.f0x1d.logfox.coil.AppIconFetcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        appIconFetcherFactory: AppIconFetcher.Factory,
    ): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(appIconFetcherFactory)
        }
        .build()
}
