package com.f0x1d.logfox.feature.terminals.impl.di

import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import com.f0x1d.logfox.feature.terminals.api.di.RootTerminal
import com.f0x1d.logfox.feature.terminals.api.di.ShizukuTerminal
import com.f0x1d.logfox.feature.terminals.impl.DefaultTerminal
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.f0x1d.logfox.feature.terminals.impl.RootTerminal as RootTerminalImpl
import com.f0x1d.logfox.feature.terminals.impl.ShizukuTerminal as ShizukuTerminalImpl

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
