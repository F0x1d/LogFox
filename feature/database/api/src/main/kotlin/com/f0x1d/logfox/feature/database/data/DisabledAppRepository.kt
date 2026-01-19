package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.model.DisabledApp
import kotlinx.coroutines.flow.Flow

interface DisabledAppRepository {
    suspend fun getAll(): List<DisabledApp>
    fun getAllAsFlow(): Flow<List<DisabledApp>>
    suspend fun getById(id: Long): DisabledApp?
    fun getByIdAsFlow(id: Long): Flow<DisabledApp?>
    suspend fun getByPackageName(packageName: String): DisabledApp?
    fun getByPackageNameAsFlow(packageName: String): Flow<DisabledApp?>
    suspend fun insert(item: DisabledApp)
    suspend fun update(item: DisabledApp)
    suspend fun delete(item: DisabledApp)
    suspend fun deleteByPackageName(packageName: String)
    suspend fun deleteAll()
}
