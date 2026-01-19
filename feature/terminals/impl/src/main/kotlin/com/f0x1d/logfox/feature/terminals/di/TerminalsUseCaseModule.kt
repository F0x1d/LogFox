package com.f0x1d.logfox.feature.terminals.di

import com.f0x1d.logfox.feature.terminals.domain.ExitTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.ExitTerminalUseCaseImpl
import com.f0x1d.logfox.feature.terminals.domain.GetDefaultTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.GetDefaultTerminalUseCaseImpl
import com.f0x1d.logfox.feature.terminals.domain.GetSelectedTerminalUseCase
import com.f0x1d.logfox.feature.terminals.domain.GetSelectedTerminalUseCaseImpl
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
    fun bindExitTerminalUseCase(
        impl: ExitTerminalUseCaseImpl,
    ): ExitTerminalUseCase
}
