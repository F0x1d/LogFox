package com.f0x1d.logfox.di

import com.f0x1d.logfox.terminals.DefaultTerminal
import com.f0x1d.logfox.terminals.RootTerminal
import com.f0x1d.logfox.terminals.ShizukuTerminal
import com.f0x1d.logfox.terminals.base.Terminal
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TerminalsModule {

    @Provides
    fun provideTerminals(
        defaultTerminal: DefaultTerminal,
        rootTerminal: RootTerminal,
        shizukuTerminal: ShizukuTerminal
    ): Array<Terminal> = arrayOf(
        defaultTerminal,
        rootTerminal,
        shizukuTerminal,
    )
}
