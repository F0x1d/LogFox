package com.f0x1d.logfox.feature.apps.picker.impl.di

import com.f0x1d.logfox.feature.apps.picker.domain.FilterAppsUseCase
import com.f0x1d.logfox.feature.apps.picker.domain.GetInstalledAppsUseCase
import com.f0x1d.logfox.feature.apps.picker.impl.data.InstalledAppsDataSource
import com.f0x1d.logfox.feature.apps.picker.impl.data.InstalledAppsDataSourceImpl
import com.f0x1d.logfox.feature.apps.picker.impl.domain.FilterAppsUseCaseImpl
import com.f0x1d.logfox.feature.apps.picker.impl.domain.GetInstalledAppsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface AppsPickerModule {

    @Binds
    fun bindInstalledAppsDataSource(impl: InstalledAppsDataSourceImpl): InstalledAppsDataSource

    @Binds
    fun bindGetInstalledAppsUseCase(impl: GetInstalledAppsUseCaseImpl): GetInstalledAppsUseCase

    @Binds
    fun bindFilterAppsUseCase(impl: FilterAppsUseCaseImpl): FilterAppsUseCase
}
