package com.f0x1d.logfox.feature.filters.api.data

import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import kotlinx.coroutines.flow.Flow

interface FiltersRepository {

    fun getAllEnabledAsFlow(): Flow<List<UserFilter>>

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

    fun getAllAsFlow(): Flow<List<UserFilter>>

    fun getByIdAsFlow(id: Long): Flow<UserFilter?>

    suspend fun getAll(): List<UserFilter>

    suspend fun getById(id: Long): UserFilter?

    suspend fun update(item: UserFilter)

    suspend fun delete(item: UserFilter)

    suspend fun clear()
}
