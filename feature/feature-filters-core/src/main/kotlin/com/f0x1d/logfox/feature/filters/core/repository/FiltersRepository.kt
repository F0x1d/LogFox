package com.f0x1d.logfox.feature.filters.core.repository

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.logline.LogLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FiltersRepository : DatabaseProxyRepository<UserFilter> {

    suspend fun create(
        including: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    )

    suspend fun createAll(userFilters: List<UserFilter>)

    suspend fun switch(userFilter: UserFilter, checked: Boolean)

    suspend fun update(
        userFilter: UserFilter,
        including: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    )
}

internal class FiltersRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : FiltersRepository {

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
        database.userFilterDao().insert(userFilters)
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
        database.userFilterDao().getAllAsFlow().flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<UserFilter?> =
        database.userFilterDao().getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<UserFilter> = withContext(ioDispatcher) {
        database.userFilterDao().getAll()
    }

    override suspend fun getById(id: Long): UserFilter? = withContext(ioDispatcher) {
        database.userFilterDao().getById(id)
    }

    override suspend fun update(item: UserFilter) = withContext(ioDispatcher) {
        database.userFilterDao().update(item)
    }

    override suspend fun delete(item: UserFilter) = withContext(ioDispatcher) {
        database.userFilterDao().delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        database.userFilterDao().deleteAll()
    }

    private fun String.nullIfEmpty() = ifEmpty { null }
}
