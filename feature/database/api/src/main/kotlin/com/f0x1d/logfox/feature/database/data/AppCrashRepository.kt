package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.model.AppCrash
import kotlinx.coroutines.flow.Flow

interface AppCrashRepository {
    fun getAllAsFlow(): Flow<List<AppCrash>>
    suspend fun getAll(): List<AppCrash>
    fun getByIdAsFlow(id: Long): Flow<AppCrash?>
    suspend fun getById(id: Long): AppCrash?
    suspend fun getAllByPackageName(packageName: String): List<AppCrash>
    suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrash>
    suspend fun insert(appCrash: AppCrash): Long
    suspend fun update(appCrash: AppCrash)
    suspend fun delete(appCrash: AppCrash)
    suspend fun deleteByPackageName(packageName: String, time: Long = System.currentTimeMillis())
    suspend fun deleteAll(time: Long = System.currentTimeMillis())
    suspend fun clearIfNeeded()
}
