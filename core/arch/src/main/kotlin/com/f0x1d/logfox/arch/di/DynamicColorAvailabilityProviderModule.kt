package com.f0x1d.logfox.arch.di

import com.f0x1d.logfox.arch.presentation.ui.fragment.compose.DynamicColorAvailabilityProvider
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DynamicColorAvailabilityProviderModule {

    @Provides
    @Singleton
    fun provideDynamicColorAvailabilityProvider(
        appPreferences: AppPreferences,
    ): DynamicColorAvailabilityProvider = object : DynamicColorAvailabilityProvider {
        override fun isDynamicColorAvailable(): Boolean = appPreferences.monetEnabled
    }
}
