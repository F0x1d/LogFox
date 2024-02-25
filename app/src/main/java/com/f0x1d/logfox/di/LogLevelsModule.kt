package com.f0x1d.logfox.di

import android.app.Activity
import com.f0x1d.logfox.model.LogLevel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object LogLevelsModule {

    @Provides
    @ActivityScoped
    fun provideLogLevelsColorsMappings(activity: Activity) = LogLevel.entries.associateWith {
        val getColor = { resId: Int ->
            activity.resources.getColor(resId, activity.theme)
        }

        getColor(it.backgroundColorId) to getColor(it.foregroundColorId)
    }
}