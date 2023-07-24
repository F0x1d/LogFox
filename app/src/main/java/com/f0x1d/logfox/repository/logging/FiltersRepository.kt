package com.f0x1d.logfox.repository.logging

import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiltersRepository @Inject constructor(private val database: AppDatabase): LoggingHelperItemsRepository<UserFilter>() {

    override suspend fun setup() = itemsFlow.update {
        database.userFilterDao().getAll()
    }

    fun create(enabledLogLevels: List<LogLevel>, pid: String, tid: String, tag: String, content: String) = createAll(
        listOf(
            UserFilter(
                enabledLogLevels,
                pid.nullIfEmpty(),
                tid.nullIfEmpty(),
                tag.nullIfEmpty(),
                content.nullIfEmpty()
            )
        )
    )

    fun createAll(userFilters: List<UserFilter>) = runOnAppScope {
        itemsFlow.updateList {
            userFilters.forEach {
                add(
                    it.copy(id = database.userFilterDao().insert(it))
                )
            }
        }
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = update {
        userFilter.copy(enabled = checked)
    }

    fun update(userFilter: UserFilter, enabledLogLevels: List<LogLevel>, pid: String, tid: String, tag: String, content: String) = update {
        userFilter.copy(
            allowedLevels = enabledLogLevels,
            pid = pid.nullIfEmpty(),
            tid = tid.nullIfEmpty(),
            tag = tag.nullIfEmpty(),
            content = content.nullIfEmpty()
        )
    }

    fun update(newValue: () -> UserFilter) = updateInternal(
        newItem = newValue,
        databaseUpdate = { database.userFilterDao().update(it) },
        idGet = { it.id }
    )

    override suspend fun deleteInternal(item: UserFilter) {
        itemsFlow.updateList {
            remove(item)
            database.userFilterDao().delete(item)
        }
    }

    override suspend fun clearInternal() {
        itemsFlow.update {
            database.userFilterDao().deleteAll()
            emptyList()
        }
    }

    private fun String.nullIfEmpty() = ifEmpty { null }
}