package com.f0x1d.logfox.terminals.base

import com.f0x1d.logfox.model.terminal.TerminalProcess
import com.f0x1d.logfox.model.terminal.TerminalResult

interface Terminal {
    val title: Int

    suspend fun isSupported(): Boolean

    suspend fun executeNow(vararg command: String): TerminalResult

    fun execute(vararg command: String): TerminalProcess?

    suspend fun exit() = Unit
}
