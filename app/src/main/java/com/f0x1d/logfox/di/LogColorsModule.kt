package com.f0x1d.logfox.di

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import com.f0x1d.logfox.extensions.logline.backgroundColorIdByLevel
import com.f0x1d.logfox.extensions.logline.foregroundColorIdByLevel
import com.f0x1d.logfox.model.LogLevel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object LogColorsModule {

    @SuppressLint("ResourceType")
    @Provides
    @FragmentScoped
    fun provideLogLevelsColorsMappings(fragment: Fragment) = LogLevel.values().associateWith {
        val getColor = { resId: Int ->
            fragment.resources.getColor(resId, fragment.requireContext().theme)
        }

        getColor(it.backgroundColorIdByLevel()) to getColor(it.foregroundColorIdByLevel())
    }
}