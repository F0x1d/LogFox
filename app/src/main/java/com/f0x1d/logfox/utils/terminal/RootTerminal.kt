package com.f0x1d.logfox.utils.terminal

import com.f0x1d.logfox.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootTerminal @Inject constructor(): DefaultTerminal() {

    companion object {
        const val INDEX = 1
    }

    override val title = R.string.root

    override val commandPrefix = arrayOf("su", "-c")

    override suspend fun isSupported() = try {
        executeNow("exit").isSuccessful
    } catch (e: Exception) {
        false
    }
}