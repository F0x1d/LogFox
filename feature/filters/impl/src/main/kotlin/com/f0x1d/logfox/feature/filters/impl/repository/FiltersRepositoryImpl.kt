package com.f0x1d.logfox.feature.filters.impl.repository

import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.filters.api.repository.FiltersRepository
import com.f0x1d.logfox.model.logline.LogLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FiltersRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : FiltersRepository {

    override fun getAllEnabledAsFlow(): Flow<List<UserFilter>> =
        database.userFilters().getAllEnabledAsFlow()
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun create(
        including: Boolean,
        enabledLogLevels: List<LogLevel>,
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

    override suspend fun createAll(userFilters: List<UserFilter>) = withContext(ioDispatcher) {
        database.userFilters().insert(userFilters)
    }

    override suspend fun switch(userFilter: UserFilter, checked: Boolean) = update {
        userFilter.copy(enabled = checked)
    }

    override suspend fun update(
        userFilter: UserFilter,
        including: Boolean,
        enabledLogLevels: List<LogLevel>,
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

    private suspend fun update(newValue: () -> UserFilter) = update(newValue())

    override fun getAllAsFlow(): Flow<List<UserFilter>> =
        database.userFilters().getAllAsFlow()
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<UserFilter?> =
        database.userFilters().getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<UserFilter> = withContext(ioDispatcher) {
        database.userFilters().getAll()
    }

    override suspend fun getById(id: Long): UserFilter? = withContext(ioDispatcher) {
        database.userFilters().getById(id)
    }

    override suspend fun update(item: UserFilter) = withContext(ioDispatcher) {
        database.userFilters().update(item)
    }

    override suspend fun delete(item: UserFilter) = withContext(ioDispatcher) {
        database.userFilters().delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        database.userFilters().deleteAll()
    }

    private fun String.nullIfEmpty() = ifEmpty { null }
}
