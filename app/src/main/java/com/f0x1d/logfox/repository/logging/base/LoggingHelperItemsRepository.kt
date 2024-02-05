package com.f0x1d.logfox.repository.logging.base

import com.f0x1d.logfox.extensions.runOnAppScope

abstract class LoggingHelperItemsRepository<T>: LoggingHelperRepository() {

    fun update(item: T) = runOnAppScope { updateInternal(item) }
    fun delete(item: T) = runOnAppScope { deleteInternal(item) }
    fun clear() = runOnAppScope { clearInternal() }

    protected abstract suspend fun updateInternal(item: T)
    protected abstract suspend fun deleteInternal(item: T)
    protected abstract suspend fun clearInternal()
}