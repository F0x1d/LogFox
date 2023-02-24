package com.f0x1d.logfox.repository.logging.base

import com.f0x1d.logfox.extensions.updateList
import kotlinx.coroutines.flow.MutableStateFlow

abstract class LoggingHelperItemsRepository<T>: LoggingHelperRepository() {

    val itemsFlow = MutableStateFlow(emptyList<T>())

    fun delete(item: T) = runOnAppScope { deleteInternal(item) }
    fun clear() = runOnAppScope { clearInternal() }

    protected abstract suspend fun deleteInternal(item: T)
    protected abstract suspend fun clearInternal()

    protected fun updateInternal(newItem: suspend () -> T, databaseUpdate: suspend (T) -> Unit, idGet: (T) -> Long) = runOnAppScope {
        itemsFlow.updateList {
            val newValue = newItem.invoke().also {
                databaseUpdate.invoke(it)
            }
            val newValueId = idGet.invoke(newValue)

            set(
                indexOfFirst { idGet.invoke(it) == newValueId },
                newValue
            )
        }
    }
}