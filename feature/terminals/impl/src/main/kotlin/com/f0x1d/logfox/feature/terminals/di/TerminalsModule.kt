package com.f0x1d.logfox.feature.terminals.di

import com.f0x1d.logfox.feature.terminals.DefaultTerminal
import com.f0x1d.logfox.feature.terminals.RootTerminal as RootTerminalImpl
import com.f0x1d.logfox.feature.terminals.ShizukuTerminal as ShizukuTerminalImpl
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object TerminalsModule {

    @Provides
    fun provideTerminals(
        defaultTerminal: DefaultTerminal,
        rootTerminal: RootTerminalImpl,
        shizukuTerminal: ShizukuTerminalImpl,
    ): Map<TerminalType, Terminal> = mapOf(
        TerminalType.Default to defaultTerminal,
        TerminalType.Root to rootTerminal,
        TerminalType.Shizuku to shizukuTerminal,
    )

    @Provides
    @RootTerminal
    fun provideRootTerminal(terminal: RootTerminalImpl): Terminal = terminal

    @Provides
    @ShizukuTerminal
    fun provideShizukuTerminal(terminal: ShizukuTerminalImpl): Terminal = terminal
}
