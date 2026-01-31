package com.f0x1d.logfox.feature.database.api.data

import com.f0x1d.logfox.feature.database.api.entity.DisabledAppEntity
import kotlinx.coroutines.flow.Flow

interface DisabledAppDataSource {

    suspend fun getAll(): List<DisabledAppEntity>

    fun getAllAsFlow(): Flow<List<DisabledAppEntity>>

    suspend fun getById(id: Long): DisabledAppEntity?

    fun getByIdAsFlow(id: Long): Flow<DisabledAppEntity?>

    suspend fun getByPackageName(packageName: String): DisabledAppEntity?

    fun getByPackageNameAsFlow(packageName: String): Flow<DisabledAppEntity?>

    suspend fun insert(item: DisabledAppEntity)

    suspend fun update(item: DisabledAppEntity)

    suspend fun delete(item: DisabledAppEntity)

    suspend fun deleteByPackageName(packageName: String)

    suspend fun deleteAll()
}
