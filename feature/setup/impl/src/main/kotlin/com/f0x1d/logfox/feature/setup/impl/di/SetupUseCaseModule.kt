package com.f0x1d.logfox.feature.setup.impl.di

import com.f0x1d.logfox.feature.setup.api.domain.CheckReadLogsPermissionUseCase
import com.f0x1d.logfox.feature.setup.api.domain.CopyToClipboardUseCase
import com.f0x1d.logfox.feature.setup.api.domain.ExecuteGrantViaRootUseCase
import com.f0x1d.logfox.feature.setup.api.domain.ExecuteGrantViaShizukuUseCase
import com.f0x1d.logfox.feature.setup.api.domain.GetAdbCommandUseCase
import com.f0x1d.logfox.feature.setup.api.domain.SelectTerminalUseCase
import com.f0x1d.logfox.feature.setup.impl.domain.CheckReadLogsPermissionUseCaseImpl
import com.f0x1d.logfox.feature.setup.impl.domain.CopyToClipboardUseCaseImpl
import com.f0x1d.logfox.feature.setup.impl.domain.ExecuteGrantViaRootUseCaseImpl
import com.f0x1d.logfox.feature.setup.impl.domain.ExecuteGrantViaShizukuUseCaseImpl
import com.f0x1d.logfox.feature.setup.impl.domain.GetAdbCommandUseCaseImpl
import com.f0x1d.logfox.feature.setup.impl.domain.SelectTerminalUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SetupUseCaseModule {

    @Binds
    fun bindExecuteGrantViaRootUseCase(
        impl: ExecuteGrantViaRootUseCaseImpl,
    ): ExecuteGrantViaRootUseCase

    @Binds
    fun bindExecuteGrantViaShizukuUseCase(
        impl: ExecuteGrantViaShizukuUseCaseImpl,
    ): ExecuteGrantViaShizukuUseCase

    @Binds
    fun bindSelectTerminalUseCase(impl: SelectTerminalUseCaseImpl): SelectTerminalUseCase

    @Binds
    fun bindCheckReadLogsPermissionUseCase(
        impl: CheckReadLogsPermissionUseCaseImpl,
    ): CheckReadLogsPermissionUseCase

    @Binds
    fun bindCopyToClipboardUseCase(impl: CopyToClipboardUseCaseImpl): CopyToClipboardUseCase

    @Binds
    fun bindGetAdbCommandUseCase(impl: GetAdbCommandUseCaseImpl): GetAdbCommandUseCase
}
