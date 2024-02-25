package com.f0x1d.logfox.utils.terminal

import com.f0x1d.logfox.R
import com.f0x1d.logfox.model.terminal.TerminalProcess
import com.f0x1d.logfox.model.terminal.TerminalResult
import com.f0x1d.logfox.utils.terminal.base.Terminal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DefaultTerminal @Inject constructor(): Terminal {

    companion object {
        const val INDEX = 0
    }

    override val title = R.string.terminal_default

    protected open val commandPrefix: Array<String>? = null

    override suspend fun isSupported() = true

    private fun createProcess(commands: Array<out String>) = (commandPrefix ?: emptyArray())
        .plus(commands)
        .let { Runtime.getRuntime().exec(it) }

    override suspend fun executeNow(vararg command: String) = withContext(Dispatchers.IO) {
        val process = createProcess(command)

        val output = async(Dispatchers.IO) {
            process.inputStream.readBytes().decodeToString()
        }
        val error = async(Dispatchers.IO) {
            process.errorStream.readBytes().decodeToString()
        }
        val exitCode = process.waitFor()

        TerminalResult(exitCode, output.await(), error.await())
    }

    override fun execute(vararg command: String) = createProcess(command).run {
        TerminalProcess(
            inputStream,
            errorStream,
            outputStream,
            this::destroy
        )
    }
}