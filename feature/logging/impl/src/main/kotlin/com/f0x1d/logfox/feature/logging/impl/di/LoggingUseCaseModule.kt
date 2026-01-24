package com.f0x1d.logfox.feature.logging.impl.di

import com.f0x1d.logfox.feature.logging.api.domain.AddLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.ClearLogsUseCase
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetFileNameUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLastLogUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsSnapshotUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetPausedFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetSelectedLogLinesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesUseCase
import com.f0x1d.logfox.feature.logging.api.domain.ReadLogFileUseCase
import com.f0x1d.logfox.feature.logging.api.domain.StartLoggingUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateLogsUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdatePausedUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateQueryUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.impl.domain.AddLogLineUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.ClearLogsUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.FormatLogLineUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetFileNameUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetLastLogUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetLogsFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetLogsSnapshotUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetPausedFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetQueryFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetSelectedLogLinesFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetShowLogValuesFlowUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.GetShowLogValuesUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.ReadLogFileUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.StartLoggingUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.UpdateLogsUseCaseImpl
import com.f0x1d.logfox.feature.logging.impl.domain.UpdatePausedUseCaseImpl
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
    fun bindGetLogsFlowUseCase(impl: GetLogsFlowUseCaseImpl): GetLogsFlowUseCase

    @Binds
    fun bindGetQueryFlowUseCase(impl: GetQueryFlowUseCaseImpl): GetQueryFlowUseCase

    @Binds
    fun bindUpdateQueryUseCase(impl: UpdateQueryUseCaseImpl): UpdateQueryUseCase

    @Binds
    fun bindGetSelectedLogLinesFlowUseCase(
        impl: GetSelectedLogLinesFlowUseCaseImpl,
    ): GetSelectedLogLinesFlowUseCase

    @Binds
    fun bindUpdateSelectedLogLinesUseCase(
        impl: UpdateSelectedLogLinesUseCaseImpl,
    ): UpdateSelectedLogLinesUseCase

    @Binds
    fun bindUpdateLogsUseCase(impl: UpdateLogsUseCaseImpl): UpdateLogsUseCase

    @Binds
    fun bindStartLoggingUseCase(impl: StartLoggingUseCaseImpl): StartLoggingUseCase

    @Binds
    fun bindFormatLogLineUseCase(impl: FormatLogLineUseCaseImpl): FormatLogLineUseCase

    @Binds
    fun bindGetShowLogValuesUseCase(impl: GetShowLogValuesUseCaseImpl): GetShowLogValuesUseCase

    @Binds
    fun bindGetShowLogValuesFlowUseCase(impl: GetShowLogValuesFlowUseCaseImpl): GetShowLogValuesFlowUseCase

    @Binds
    fun bindGetPausedFlowUseCase(impl: GetPausedFlowUseCaseImpl): GetPausedFlowUseCase

    @Binds
    fun bindUpdatePausedUseCase(impl: UpdatePausedUseCaseImpl): UpdatePausedUseCase

    @Binds
    fun bindAddLogLineUseCase(impl: AddLogLineUseCaseImpl): AddLogLineUseCase

    @Binds
    fun bindClearLogsUseCase(impl: ClearLogsUseCaseImpl): ClearLogsUseCase

    @Binds
    fun bindGetLogsSnapshotUseCase(impl: GetLogsSnapshotUseCaseImpl): GetLogsSnapshotUseCase

    @Binds
    fun bindGetLastLogUseCase(impl: GetLastLogUseCaseImpl): GetLastLogUseCase

    @Binds
    fun bindReadLogFileUseCase(impl: ReadLogFileUseCaseImpl): ReadLogFileUseCase

    @Binds
    fun bindGetFileNameUseCase(impl: GetFileNameUseCaseImpl): GetFileNameUseCase
}
