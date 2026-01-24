package com.f0x1d.logfox.feature.terminals

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import com.f0x1d.logfox.feature.terminals.impl.R
import com.f0x1d.logfox.feature.terminals.model.TerminalProcess
import com.f0x1d.logfox.feature.terminals.model.TerminalResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal open class DefaultTerminal @Inject constructor(
    @IODispatcher protected val ioDispatcher: CoroutineDispatcher,
) : Terminal {

    override val type = TerminalType.Default
    override val title = R.string.terminal_default

    override suspend fun isSupported() = true

    override suspend fun executeNow(vararg command: String) = withContext(ioDispatcher) {
        val process = Runtime.getRuntime().exec(command)

        val output = async {
            process.inputStream.readBytes().decodeToString()
        }
        val error = async {
            process.errorStream.readBytes().decodeToString()
        }
        val exitCode = process.waitFor()

        TerminalResult(exitCode, output.await(), error.await())
    }

    override fun execute(vararg command: String) = Runtime.getRuntime().exec(command).run {
        TerminalProcess(
            output = inputStream,
            error = errorStream,
            input = outputStream,
            destroy = this::destroy,
        )
    }
}
