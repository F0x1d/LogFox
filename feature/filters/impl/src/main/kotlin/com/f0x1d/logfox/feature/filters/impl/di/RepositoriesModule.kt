package com.f0x1d.logfox.feature.filters.impl.di

import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.impl.data.FiltersRepositoryImpl
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
