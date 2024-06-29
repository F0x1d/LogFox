package com.f0x1d.logfox.terminals

import com.f0x1d.logfox.arch.di.IODispatcher
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

    override suspend fun isSupported() = withContext(ioDispatcher) {
        try {
            executeNow("exit").isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
