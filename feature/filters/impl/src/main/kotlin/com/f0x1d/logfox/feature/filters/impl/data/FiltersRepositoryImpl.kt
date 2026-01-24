package com.f0x1d.logfox.feature.filters.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.database.data.UserFilterDataSource
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.filters.impl.mapper.toDomain
import com.f0x1d.logfox.feature.filters.impl.mapper.toEntity
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FiltersRepositoryImpl @Inject constructor(
    private val userFilterDataSource: UserFilterDataSource,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : FiltersRepository {

    override fun getAllEnabledAsFlow(): Flow<List<UserFilter>> = userFilterDataSource.getAllEnabledAsFlow()
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override suspend fun create(
        including: Boolean,
        enabled: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    ) = createAll(
        listOf(
            UserFilter(
                including = including,
                enabled = enabled,
                allowedLevels = enabledLogLevels,
                uid = uid?.nullIfEmpty(),
                pid = pid?.nullIfEmpty(),
                tid = tid?.nullIfEmpty(),
                packageName = packageName?.nullIfEmpty(),
                tag = tag?.nullIfEmpty(),
                content = content?.nullIfEmpty(),
            ),
        ),
    )

    override suspend fun createAll(userFilters: List<UserFilter>) = withContext(ioDispatcher) {
        userFilterDataSource.insert(userFilters.map { it.toEntity() })
    }

    override suspend fun switch(userFilter: UserFilter, checked: Boolean) = update {
        userFilter.copy(enabled = checked)
    }

    override suspend fun update(
        userFilter: UserFilter,
        including: Boolean,
        enabled: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    ) = update {
        userFilter.copy(
            including = including,
            enabled = enabled,
            allowedLevels = enabledLogLevels,
            uid = uid?.nullIfEmpty(),
            pid = pid?.nullIfEmpty(),
            tid = tid?.nullIfEmpty(),
            packageName = packageName?.nullIfEmpty(),
            tag = tag?.nullIfEmpty(),
            content = content?.nullIfEmpty(),
        )
    }

    private suspend fun update(newValue: () -> UserFilter) = update(newValue())

    override fun getAllAsFlow(): Flow<List<UserFilter>> = userFilterDataSource.getAllAsFlow()
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<UserFilter?> = userFilterDataSource.getByIdAsFlow(id)
        .map { it?.toDomain() }
        .flowOn(ioDispatcher)

    override suspend fun getAll(): List<UserFilter> = withContext(ioDispatcher) {
        userFilterDataSource.getAll().map { it.toDomain() }
    }

    override suspend fun getById(id: Long): UserFilter? = withContext(ioDispatcher) {
        userFilterDataSource.getById(id)?.toDomain()
    }

    override suspend fun update(item: UserFilter) = withContext(ioDispatcher) {
        userFilterDataSource.update(item.toEntity())
    }

    override suspend fun delete(item: UserFilter) = withContext(ioDispatcher) {
        userFilterDataSource.delete(item.toEntity())
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        userFilterDataSource.deleteAll()
    }

    private fun String.nullIfEmpty() = ifEmpty { null }
}
