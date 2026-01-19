package com.f0x1d.logfox.feature.logging.impl.di

import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetSelectedLogLinesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.StartLoggingUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateLogsUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateQueryUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.impl.domain.GetLogsFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetQueryFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetSelectedLogLinesFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.StartLoggingUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.UpdateLogsUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.UpdateQueryUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.UpdateSelectedLogLinesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface LoggingUseCaseModule {

    @Binds
    fun bindGetLogsFlowUseCase(
        impl: GetLogsFlowUseCaseImpl,
    ): GetLogsFlowUseCase

    @Binds
    fun bindGetQueryFlowUseCase(
        impl: GetQueryFlowUseCaseImpl,
    ): GetQueryFlowUseCase

    @Binds
    fun bindUpdateQueryUseCase(
        impl: UpdateQueryUseCaseImpl,
    ): UpdateQueryUseCase

    @Binds
    fun bindGetSelectedLogLinesFlowUseCase(
        impl: GetSelectedLogLinesFlowUseCaseImpl,
    ): GetSelectedLogLinesFlowUseCase

    @Binds
    fun bindUpdateSelectedLogLinesUseCase(
        impl: UpdateSelectedLogLinesUseCaseImpl,
    ): UpdateSelectedLogLinesUseCase

    @Binds
    fun bindUpdateLogsUseCase(
        impl: UpdateLogsUseCaseImpl,
    ): UpdateLogsUseCase

    @Binds
    fun bindStartLoggingUseCase(
        impl: StartLoggingUseCaseImpl,
    ): StartLoggingUseCase
}
