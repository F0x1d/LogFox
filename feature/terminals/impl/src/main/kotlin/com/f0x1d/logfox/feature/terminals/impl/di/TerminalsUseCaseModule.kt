package com.f0x1d.logfox.feature.terminals.impl.di

import com.f0x1d.logfox.feature.terminals.api.domain.ExitTerminalUseCase
import com.f0x1d.logfox.feature.terminals.api.domain.GetDefaultTerminalUseCase
import com.f0x1d.logfox.feature.terminals.api.domain.GetSelectedTerminalUseCase
import com.f0x1d.logfox.feature.terminals.impl.domain.ExitTerminalUseCaseImpl
import com.f0x1d.logfox.feature.terminals.impl.domain.GetDefaultTerminalUseCaseImpl
import com.f0x1d.logfox.feature.terminals.impl.domain.GetSelectedTerminalUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface TerminalsUseCaseModule {

    @Binds
    fun bindGetSelectedTerminalUseCase(
        impl: GetSelectedTerminalUseCaseImpl,
    ): GetSelectedTerminalUseCase

    @Binds
    fun bindGetDefaultTerminalUseCase(
        impl: GetDefaultTerminalUseCaseImpl,
    ): GetDefaultTerminalUseCase

    @Binds
    fun bindExitTerminalUseCase(impl: ExitTerminalUseCaseImpl): ExitTerminalUseCase
}
