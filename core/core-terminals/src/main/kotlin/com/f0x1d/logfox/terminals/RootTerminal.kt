package com.f0x1d.logfox.terminals

import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.model.terminal.TerminalResult
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootTerminal @Inject constructor(
    @IODispatcher ioDispatcher: CoroutineDispatcher,
): DefaultTerminal(ioDispatcher) {

    companion object {
        const val INDEX = 1
    }

    override val title = R.string.root

    override val commandPrefix = arrayOf("su", "-c")

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

    // can't make currently execute method with libsu

    override suspend fun exit() = withContext(ioDispatcher) {
        Shell.getShell().waitAndClose()
    }
}
