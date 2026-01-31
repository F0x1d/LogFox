package com.f0x1d.logfox.feature.filters.impl.di

import com.f0x1d.logfox.feature.filters.api.domain.ClearAllFiltersUseCase
import com.f0x1d.logfox.feature.filters.api.domain.CreateAllFiltersUseCase
import com.f0x1d.logfox.feature.filters.api.domain.CreateFilterUseCase
import com.f0x1d.logfox.feature.filters.api.domain.DeleteFilterUseCase
import com.f0x1d.logfox.feature.filters.api.domain.ExportFiltersToUriUseCase
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.domain.GetAllFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.domain.GetFilterByIdFlowUseCase
import com.f0x1d.logfox.feature.filters.api.domain.ImportFiltersFromUriUseCase
import com.f0x1d.logfox.feature.filters.api.domain.SwitchFilterUseCase
import com.f0x1d.logfox.feature.filters.api.domain.UpdateFilterUseCase
import com.f0x1d.logfox.feature.filters.impl.domain.ClearAllFiltersUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.CreateAllFiltersUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.CreateFilterUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.DeleteFilterUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.ExportFiltersToUriUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.GetAllEnabledFiltersFlowUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.GetAllFiltersFlowUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.GetFilterByIdFlowUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.ImportFiltersFromUriUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.SwitchFilterUseCaseImpl
import com.f0x1d.logfox.feature.filters.impl.domain.UpdateFilterUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FiltersUseCaseModule {

    @Binds
    fun bindGetAllFiltersFlowUseCase(
        getAllFiltersFlowUseCaseImpl: GetAllFiltersFlowUseCaseImpl,
    ): GetAllFiltersFlowUseCase

    @Binds
    fun bindGetAllEnabledFiltersFlowUseCase(
        getAllEnabledFiltersFlowUseCaseImpl: GetAllEnabledFiltersFlowUseCaseImpl,
    ): GetAllEnabledFiltersFlowUseCase

    @Binds
    fun bindGetFilterByIdFlowUseCase(
        getFilterByIdFlowUseCaseImpl: GetFilterByIdFlowUseCaseImpl,
    ): GetFilterByIdFlowUseCase

    @Binds
    fun bindCreateFilterUseCase(
        createFilterUseCaseImpl: CreateFilterUseCaseImpl,
    ): CreateFilterUseCase

    @Binds
    fun bindCreateAllFiltersUseCase(
        createAllFiltersUseCaseImpl: CreateAllFiltersUseCaseImpl,
    ): CreateAllFiltersUseCase

    @Binds
    fun bindUpdateFilterUseCase(
        updateFilterUseCaseImpl: UpdateFilterUseCaseImpl,
    ): UpdateFilterUseCase

    @Binds
    fun bindSwitchFilterUseCase(
        switchFilterUseCaseImpl: SwitchFilterUseCaseImpl,
    ): SwitchFilterUseCase

    @Binds
    fun bindDeleteFilterUseCase(
        deleteFilterUseCaseImpl: DeleteFilterUseCaseImpl,
    ): DeleteFilterUseCase

    @Binds
    fun bindClearAllFiltersUseCase(
        clearAllFiltersUseCaseImpl: ClearAllFiltersUseCaseImpl,
    ): ClearAllFiltersUseCase

    @Binds
    fun bindExportFiltersToUriUseCase(
        exportFiltersToUriUseCaseImpl: ExportFiltersToUriUseCaseImpl,
    ): ExportFiltersToUriUseCase

    @Binds
    fun bindImportFiltersFromUriUseCase(
        importFiltersFromUriUseCaseImpl: ImportFiltersFromUriUseCaseImpl,
    ): ImportFiltersFromUriUseCase
}
