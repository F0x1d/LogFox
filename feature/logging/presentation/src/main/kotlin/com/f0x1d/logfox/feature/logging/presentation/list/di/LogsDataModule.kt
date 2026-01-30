package com.f0x1d.logfox.feature.logging.presentation.list.di

import com.f0x1d.logfox.feature.logging.presentation.list.data.LogsExpandedRepositoryImpl
import com.f0x1d.logfox.feature.logging.presentation.list.data.LogsSelectionRepositoryImpl
import com.f0x1d.logfox.feature.logging.presentation.list.domain.LogsExpandedRepository
import com.f0x1d.logfox.feature.logging.presentation.list.domain.LogsSelectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal interface LogsDataModule {

    @Binds
    @ViewModelScoped
    fun bindSelectionRepository(impl: LogsSelectionRepositoryImpl): LogsSelectionRepository

    @Binds
    @ViewModelScoped
    fun bindExpandedRepository(impl: LogsExpandedRepositoryImpl): LogsExpandedRepository
}
