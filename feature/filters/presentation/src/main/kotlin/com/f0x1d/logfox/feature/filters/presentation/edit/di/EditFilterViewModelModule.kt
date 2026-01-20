package com.f0x1d.logfox.feature.filters.presentation.edit.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object EditFilterViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideEditFilterArgs(savedStateHandle: SavedStateHandle) = EditFilterArgs(
        filterId = savedStateHandle.get<Long>("filter_id"),
        uid = savedStateHandle.get<String>("log_uid"),
        pid = savedStateHandle.get<String>("log_pid"),
        tid = savedStateHandle.get<String>("log_tid"),
        packageName = savedStateHandle.get<String>("log_package_name"),
        tag = savedStateHandle.get<String>("log_tag"),
        content = savedStateHandle.get<String>("log_content"),
        level = savedStateHandle.get<Int>("log_level"),
    )
}
