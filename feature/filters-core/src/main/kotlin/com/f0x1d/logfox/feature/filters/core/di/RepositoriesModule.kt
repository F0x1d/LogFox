package com.f0x1d.logfox.feature.filters.core.di

import com.f0x1d.logfox.feature.filters.core.repository.FiltersRepository
import com.f0x1d.logfox.feature.filters.core.repository.FiltersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun bindFiltersRepository(
        filtersRepositoryImpl: FiltersRepositoryImpl,
    ): FiltersRepository
}
