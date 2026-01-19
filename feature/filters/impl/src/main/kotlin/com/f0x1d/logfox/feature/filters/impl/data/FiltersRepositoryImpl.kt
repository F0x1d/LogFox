package com.f0x1d.logfox.feature.filters.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.database.data.UserFilterRepository
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FiltersRepositoryImpl @Inject constructor(
    private val userFilterRepository: UserFilterRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : FiltersRepository {

    override fun getAllEnabledAsFlow(): Flow<List<UserFilter>> = userFilterRepository.getAllEnabledAsFlow()
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
        content: String?,
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
                content = content?.nullIfEmpty(),
            ),
        ),
    )

    override suspend fun createAll(userFilters: List<UserFilter>) = withContext(ioDispatcher) {
        userFilterRepository.insert(userFilters)
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
        content: String?,
    ) = update {
        userFilter.copy(
            including = including,
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

    override fun getAllAsFlow(): Flow<List<UserFilter>> = userFilterRepository.getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<UserFilter?> = userFilterRepository.getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<UserFilter> = withContext(ioDispatcher) {
        userFilterRepository.getAll()
    }

    override suspend fun getById(id: Long): UserFilter? = withContext(ioDispatcher) {
        userFilterRepository.getById(id)
    }

    override suspend fun update(item: UserFilter) = withContext(ioDispatcher) {
        userFilterRepository.update(item)
    }

    override suspend fun delete(item: UserFilter) = withContext(ioDispatcher) {
        userFilterRepository.delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        userFilterRepository.deleteAll()
    }

    private fun String.nullIfEmpty() = ifEmpty { null }
}
