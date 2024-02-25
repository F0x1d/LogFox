package com.f0x1d.logfox.repository.logging.base

abstract class LoggingHelperItemsRepository<T>: LoggingHelperRepository() {

    fun update(item: T) = runOnRepoScope { updateInternal(item) }
    fun delete(item: T) = runOnRepoScope { deleteInternal(item) }
    fun clear() = runOnRepoScope { clearInternal() }

    protected abstract suspend fun updateInternal(item: T)
    protected abstract suspend fun deleteInternal(item: T)
    protected abstract suspend fun clearInternal()
}