package com.f0x1d.logfox.repository.logging.base

import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.repository.logging.readers.base.LogsReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class LoggingHelperRepository: BaseRepository() {

    open val readers = emptyList<LogsReader>()
    protected lateinit var repositoryScope: CoroutineScope

    open suspend fun setup() {
        repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    protected fun onRepoScope(block: suspend () -> Unit) = repositoryScope.launch {
        block()
    }

    protected fun runOnRepoScope(block: suspend () -> Unit) {
        repositoryScope.launch { block() }
    }

    open suspend fun stop() {
        repositoryScope.cancel()
    }
}