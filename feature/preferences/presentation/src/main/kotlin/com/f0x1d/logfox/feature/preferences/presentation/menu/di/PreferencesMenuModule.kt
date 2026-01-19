package com.f0x1d.logfox.feature.preferences.presentation.menu.di

import android.content.Context
import com.f0x1d.logfox.core.logging.timberLogFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File

@Module
@InstallIn(ViewModelComponent::class)
internal object PreferencesMenuModule {

    @Provides
    fun provideTimberLogFile(@ApplicationContext context: Context): File = context.timberLogFile
}
