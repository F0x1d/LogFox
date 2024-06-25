package com.f0x1d.logfox.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TerminalsModule {

    @Provides
    @Singleton
    fun provideTerminals(
        defaultTerminal: com.f0x1d.logfox.terminals.DefaultTerminal,
        rootTerminal: com.f0x1d.logfox.terminals.RootTerminal,
        shizukuTerminal: com.f0x1d.logfox.terminals.ShizukuTerminal
    ): Array<com.f0x1d.logfox.terminals.base.Terminal> = arrayOf(
        defaultTerminal,
        rootTerminal,
        shizukuTerminal
    )
}
