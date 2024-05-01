package com.f0x1d.logfox.repository.logging

import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiltersRepository @Inject constructor(
    private val database: AppDatabase
): LoggingHelperItemsRepository<UserFilter>() {

    fun create(
        including: Boolean,
        enabledLogLevels: List<com.f0x1d.logfox.model.LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?
    ) = createAll(
        listOf(
            UserFilter(
                including = including,
                allowedLevels = enabledLogLevels,
                uid = uid?.nullIfEmpty(),
                pid = pid?.nullIfEmpty(),
                tid = tid?.nullIfEmpty(),
                packageName = packageName?.nullIfEmpty(),
                tag = tag?.nullIfEmpty(),
                content = content?.nullIfEmpty()
            )
        )
    )

    fun createAll(userFilters: List<UserFilter>) = runOnRepoScope {
        database.userFilterDao().insert(userFilters)
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = update {
        userFilter.copy(enabled = checked)
    }

    fun update(
        userFilter: UserFilter,
        including: Boolean,
        enabledLogLevels: List<com.f0x1d.logfox.model.LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?
    ) = update {
        userFilter.copy(
            including = including,
            allowedLevels = enabledLogLevels,
            uid = uid?.nullIfEmpty(),
            pid = pid?.nullIfEmpty(),
            tid = tid?.nullIfEmpty(),
            packageName = packageName?.nullIfEmpty(),
            tag = tag?.nullIfEmpty(),
            content = content?.nullIfEmpty()
        )
    }

    private fun update(newValue: () -> UserFilter) = update(newValue())

    override suspend fun updateInternal(item: UserFilter) = database.userFilterDao().update(item)
    override suspend fun deleteInternal(item: UserFilter) = database.userFilterDao().delete(item)

    override suspend fun clearInternal() = database.userFilterDao().deleteAll()

    private fun String.nullIfEmpty() = ifEmpty { null }
}
