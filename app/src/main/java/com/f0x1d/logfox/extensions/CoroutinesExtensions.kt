package com.f0x1d.logfox.extensions

import com.f0x1d.logfox.LogFoxApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun onAppScope(block: suspend CoroutineScope.() -> Unit) = LogFoxApp.applicationScope.launch(
    context = Dispatchers.IO
) {
    block(this)
}

fun runOnAppScope(block: suspend CoroutineScope.() -> Unit) {
    onAppScope(block)
}