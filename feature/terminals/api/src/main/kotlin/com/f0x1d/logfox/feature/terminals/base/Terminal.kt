package com.f0x1d.logfox.feature.terminals.base

import com.f0x1d.logfox.feature.terminals.model.TerminalProcess
import com.f0x1d.logfox.feature.terminals.model.TerminalResult

interface Terminal {
    val type: TerminalType
    val title: Int

    suspend fun isSupported(): Boolean

    suspend fun executeNow(vararg command: String): TerminalResult

    fun execute(vararg command: String): TerminalProcess?

    suspend fun exit() = Unit
}
