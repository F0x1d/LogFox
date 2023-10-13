package com.f0x1d.logfox.di.view

import android.view.View
import com.f0x1d.logfox.model.LogLevel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent
import dagger.hilt.android.scopes.ViewScoped

@Module
@InstallIn(ViewComponent::class)
object LogLevelViewModule {

    @Provides
    @ViewScoped
    fun provideLogLevelsColorsMappings(view: View) = LogLevel.values().associateWith {
        val getColor = { resId: Int ->
            view.resources.getColor(resId, view.context.theme)
        }

        getColor(it.backgroundColorId) to getColor(it.foregroundColorId)
    }
}