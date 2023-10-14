package com.f0x1d.logfox.repository.base

import com.f0x1d.logfox.LogFoxApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseRepository {
    protected fun onAppScope(block: suspend CoroutineScope.() -> Unit) = LogFoxApp.applicationScope.launch(Dispatchers.IO) {
        block(this)
    }

    protected fun runOnAppScope(block: suspend CoroutineScope.() -> Unit) {
        onAppScope(block)
    }
}