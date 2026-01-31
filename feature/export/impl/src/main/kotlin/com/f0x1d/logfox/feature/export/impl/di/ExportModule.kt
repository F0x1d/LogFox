package com.f0x1d.logfox.feature.export.impl.di

import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.export.api.domain.ExportContentToUriUseCase
import com.f0x1d.logfox.feature.export.api.domain.ImportContentFromUriUseCase
import com.f0x1d.logfox.feature.export.impl.data.ExportLocalDataSource
import com.f0x1d.logfox.feature.export.impl.data.ExportLocalDataSourceImpl
import com.f0x1d.logfox.feature.export.impl.data.ExportRepositoryImpl
import com.f0x1d.logfox.feature.export.impl.domain.ExportContentToUriUseCaseImpl
import com.f0x1d.logfox.feature.export.impl.domain.ImportContentFromUriUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ExportModule {

    @Binds
    fun bindExportLocalDataSource(impl: ExportLocalDataSourceImpl): ExportLocalDataSource

    @Binds
    fun bindExportRepository(impl: ExportRepositoryImpl): ExportRepository

    @Binds
    fun bindExportContentToUriUseCase(impl: ExportContentToUriUseCaseImpl): ExportContentToUriUseCase

    @Binds
    fun bindImportContentFromUriUseCase(impl: ImportContentFromUriUseCaseImpl): ImportContentFromUriUseCase
}
