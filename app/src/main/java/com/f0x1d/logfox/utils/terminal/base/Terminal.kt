package com.f0x1d.logfox.utils.terminal.base

import java.io.InputStream
import java.io.OutputStream

interface Terminal {
    val title: Int

    suspend fun isSupported(): Boolean

    suspend fun executeNow(vararg command: String): TerminalResult

    fun execute(vararg command: String): TerminalProcess?
}

data class TerminalResult(
    val exitCode: Int = 0,
    val output: String = "",
    val errorOutput: String = ""
) {
    val isSuccessful get() = exitCode == 0
}

data class TerminalProcess(
    val output: InputStream,
    val error: InputStream,
    val input: OutputStream,
    val destroy: () -> Unit
)