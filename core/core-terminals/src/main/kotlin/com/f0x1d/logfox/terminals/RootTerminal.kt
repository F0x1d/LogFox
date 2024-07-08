package com.f0x1d.logfox.terminals

import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.model.terminal.TerminalProcess
import com.f0x1d.logfox.model.terminal.TerminalResult
import com.f0x1d.logfox.terminals.base.Terminal
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootTerminal @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : Terminal {

    companion object {
        const val INDEX = 1
    }

    override val title = R.string.root

    override suspend fun isSupported(): Boolean = withContext(ioDispatcher) {
        Shell.getShell().isRoot
    }

    override suspend fun executeNow(vararg command: String): TerminalResult = withContext(ioDispatcher) {
        Shell.cmd(command.joinToString(" ")).exec().run {
            TerminalResult(
                exitCode = code,
                output = out.joinToString("\n"),
                errorOutput = err.joinToString("\n"),
            )
        }
    }

    // It seems not all devices support "su -c ...".
    // I got feedback that LogFox does not read logs, while other apps do.
    // After I implemented libsu permission could be got. So i took a look at libsu code
    // and tried to do the same here
    private fun createProcess(commands: Array<out String>): Process? = runCatching {
        val process = Runtime.getRuntime().exec("su")

        process.outputStream.run {
            write(commands.joinToString(" ").encodeToByteArray())
            write('\n'.code)
            flush()
        }

        process
    }.getOrNull()

    override fun execute(vararg command: String): TerminalProcess? = createProcess(command)?.run {
        TerminalProcess(
            output = inputStream,
            error = errorStream,
            input = outputStream,
            destroy = this::destroy,
        )
    }

    override suspend fun exit() = withContext(ioDispatcher) {
        Shell.getShell().waitAndClose()
    }
}
