package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.feature.database.model.DisabledApp
import kotlinx.coroutines.flow.Flow

interface DisabledAppsRepository {
    suspend fun isDisabledFor(packageName: String): Boolean
    fun disabledForFlow(packageName: String): Flow<Boolean>

    suspend fun checkApp(packageName: String)
    suspend fun checkApp(packageName: String, checked: Boolean)

    fun getAllAsFlow(): Flow<List<DisabledApp>>

    fun getByIdAsFlow(id: Long): Flow<DisabledApp?>

    suspend fun getAll(): List<DisabledApp>

    suspend fun getById(id: Long): DisabledApp?

    suspend fun update(item: DisabledApp)

    suspend fun delete(item: DisabledApp)

    suspend fun clear()
}
