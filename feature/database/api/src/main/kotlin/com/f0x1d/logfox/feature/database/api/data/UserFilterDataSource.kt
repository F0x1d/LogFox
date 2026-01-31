package com.f0x1d.logfox.feature.database.api.data

import com.f0x1d.logfox.feature.database.api.entity.UserFilterEntity
import kotlinx.coroutines.flow.Flow

interface UserFilterDataSource {

    fun getAllAsFlow(): Flow<List<UserFilterEntity>>

    fun getAllEnabledAsFlow(): Flow<List<UserFilterEntity>>

    suspend fun getAll(): List<UserFilterEntity>

    fun getByIdAsFlow(id: Long): Flow<UserFilterEntity?>

    suspend fun getById(id: Long): UserFilterEntity?

    suspend fun insert(userFilter: UserFilterEntity)

    suspend fun insert(items: List<UserFilterEntity>)

    suspend fun update(userFilter: UserFilterEntity)

    suspend fun delete(userFilter: UserFilterEntity)

    suspend fun deleteAll()
}
