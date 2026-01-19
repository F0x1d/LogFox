package com.f0x1d.logfox.core.presentation.di

import android.app.Application
import com.f0x1d.logfox.core.presentation.interceptor.FontsInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.inflationx.viewpump.ViewPump
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewPumpModule {

    @Provides
    @Singleton
    fun provideViewPump(application: Application) = ViewPump.builder()
        .addInterceptor(FontsInterceptor(application))
        .build()
}
