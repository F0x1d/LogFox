package com.f0x1d.logfox.feature.database.api.data

import com.f0x1d.logfox.feature.database.api.entity.AppCrashEntity
import kotlinx.coroutines.flow.Flow

interface AppCrashDataSource {

    fun getAllAsFlow(deleted: Boolean = false): Flow<List<AppCrashEntity>>

    suspend fun getAll(deleted: Boolean = false): List<AppCrashEntity>

    fun getByIdAsFlow(id: Long): Flow<AppCrashEntity?>

    suspend fun getById(id: Long): AppCrashEntity?

    suspend fun getAllByPackageName(packageName: String): List<AppCrashEntity>

    suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrashEntity>

    suspend fun insert(appCrash: AppCrashEntity): Long

    suspend fun update(appCrash: AppCrashEntity)

    suspend fun delete(appCrash: AppCrashEntity)

    suspend fun deleteByPackageName(packageName: String, time: Long = System.currentTimeMillis())

    suspend fun deleteAll(time: Long = System.currentTimeMillis())

    suspend fun clearIfNeeded()
}
