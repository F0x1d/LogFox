package com.f0x1d.logfox.terminals

import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.model.terminal.TerminalProcess
import com.f0x1d.logfox.model.terminal.TerminalResult
import com.f0x1d.logfox.terminals.base.Terminal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DefaultTerminal @Inject constructor(
    @IODispatcher protected val ioDispatcher: CoroutineDispatcher,
): Terminal {

    companion object {
        const val INDEX = 0
    }

    override val title = R.string.terminal_default

    protected open val commandPrefix: Array<String>? = null

    override suspend fun isSupported() = true

    private fun createProcess(commands: Array<out String>) = (commandPrefix ?: emptyArray())
        .plus(commands)
        .let(Runtime.getRuntime()::exec)

    override suspend fun executeNow(vararg command: String) = withContext(ioDispatcher) {
        val process = createProcess(command)

        val output = async {
            process.inputStream.readBytes().decodeToString()
        }
        val error = async {
            process.errorStream.readBytes().decodeToString()
        }
        val exitCode = process.waitFor()

        TerminalResult(exitCode, output.await(), error.await())
    }

    override fun execute(vararg command: String) = createProcess(command).run {
        TerminalProcess(
            output = inputStream,
            error = errorStream,
            input = outputStream,
            destroy = this::destroy,
        )
    }
}
