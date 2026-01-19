package com.f0x1d.logfox.di

import com.f0x1d.logfox.core.ui.base.DynamicColorAvailabilityProvider
import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
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
        uiSettingsRepository: UISettingsRepository,
    ): DynamicColorAvailabilityProvider = object : DynamicColorAvailabilityProvider {
        override fun isDynamicColorAvailable(): Boolean = uiSettingsRepository.monetEnabled().value
    }
}
