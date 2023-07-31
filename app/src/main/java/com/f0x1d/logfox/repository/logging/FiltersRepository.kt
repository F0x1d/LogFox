package com.f0x1d.logfox.repository.logging

import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiltersRepository @Inject constructor(
    private val database: AppDatabase
): LoggingHelperItemsRepository<UserFilter>() {

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
        database.userFilterDao().insert(userFilters)
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

    private fun update(newValue: () -> UserFilter) = update(newValue())

    override suspend fun updateInternal(item: UserFilter) = database.userFilterDao().update(item)
    override suspend fun deleteInternal(item: UserFilter) = database.userFilterDao().delete(item)

    override suspend fun clearInternal() = database.userFilterDao().deleteAll()

    private fun String.nullIfEmpty() = ifEmpty { null }
}