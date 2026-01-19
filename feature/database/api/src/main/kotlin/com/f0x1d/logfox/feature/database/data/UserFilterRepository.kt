package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.model.UserFilter
import kotlinx.coroutines.flow.Flow

interface UserFilterRepository {
    fun getAllAsFlow(): Flow<List<UserFilter>>
    fun getAllEnabledAsFlow(): Flow<List<UserFilter>>
    suspend fun getAll(): List<UserFilter>
    fun getByIdAsFlow(id: Long): Flow<UserFilter?>
    suspend fun getById(id: Long): UserFilter?
    suspend fun insert(userFilter: UserFilter)
    suspend fun insert(items: List<UserFilter>)
    suspend fun update(userFilter: UserFilter)
    suspend fun delete(userFilter: UserFilter)
    suspend fun deleteAll()
}
