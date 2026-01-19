package com.f0x1d.logfox.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
internal object UtilsModule {

    @NullString
    @Provides
    fun provideNullString(): String? = null
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NullString
