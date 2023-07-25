package com.f0x1d.logfox.di

import com.f0x1d.logfox.utils.terminal.DefaultTerminal
import com.f0x1d.logfox.utils.terminal.RootTerminal
import com.f0x1d.logfox.utils.terminal.base.Terminal
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
        defaultTerminal: DefaultTerminal,
        rootTerminal: RootTerminal
    ): Array<Terminal> = arrayOf(
        defaultTerminal,
        rootTerminal
    )
}