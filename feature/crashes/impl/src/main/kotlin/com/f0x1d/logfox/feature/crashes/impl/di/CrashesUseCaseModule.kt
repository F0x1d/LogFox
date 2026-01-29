package com.f0x1d.logfox.feature.crashes.impl.di

import com.f0x1d.logfox.feature.crashes.api.domain.ClearAllCrashesUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteAllCrashesByPackageNameUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToFileUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToZipUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllCrashesFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashByIdFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashesSearchQueryFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.ProcessLogLineCrashesUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.UpdateCrashesSearchQueryUseCase
import com.f0x1d.logfox.feature.crashes.impl.domain.ClearAllCrashesUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.DeleteAllCrashesByPackageNameUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.DeleteCrashUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.ExportCrashToFileUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.ExportCrashToZipUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.GetAllCrashesFlowUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.GetCrashByIdFlowUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.GetCrashesSearchQueryFlowUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.ProcessLogLineCrashesUseCaseImpl
import com.f0x1d.logfox.feature.crashes.impl.domain.UpdateCrashesSearchQueryUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface CrashesUseCaseModule {

    @Binds
    fun bindGetAllCrashesFlowUseCase(impl: GetAllCrashesFlowUseCaseImpl): GetAllCrashesFlowUseCase

    @Binds
    fun bindGetCrashByIdFlowUseCase(impl: GetCrashByIdFlowUseCaseImpl): GetCrashByIdFlowUseCase

    @Binds
    fun bindDeleteCrashUseCase(impl: DeleteCrashUseCaseImpl): DeleteCrashUseCase

    @Binds
    fun bindDeleteAllCrashesByPackageNameUseCase(
        impl: DeleteAllCrashesByPackageNameUseCaseImpl,
    ): DeleteAllCrashesByPackageNameUseCase

    @Binds
    fun bindClearAllCrashesUseCase(impl: ClearAllCrashesUseCaseImpl): ClearAllCrashesUseCase

    @Binds
    fun bindUpdateCrashesSearchQueryUseCase(
        impl: UpdateCrashesSearchQueryUseCaseImpl,
    ): UpdateCrashesSearchQueryUseCase

    @Binds
    fun bindGetCrashesSearchQueryFlowUseCase(
        impl: GetCrashesSearchQueryFlowUseCaseImpl,
    ): GetCrashesSearchQueryFlowUseCase

    @Binds
    fun bindProcessLogLineCrashesUseCase(
        impl: ProcessLogLineCrashesUseCaseImpl,
    ): ProcessLogLineCrashesUseCase

    @Binds
    fun bindExportCrashToFileUseCase(
        impl: ExportCrashToFileUseCaseImpl,
    ): ExportCrashToFileUseCase

    @Binds
    fun bindExportCrashToZipUseCase(
        impl: ExportCrashToZipUseCaseImpl,
    ): ExportCrashToZipUseCase
}
