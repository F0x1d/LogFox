package com.f0x1d.logfox.feature.filters.api.data

import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.logline.LogLevel
import kotlinx.coroutines.flow.Flow

interface FiltersRepository : DatabaseProxyRepository<UserFilter> {

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
}
