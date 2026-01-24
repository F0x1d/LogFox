package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import kotlinx.coroutines.flow.Flow

interface CrashesRepository {
    fun getAllAsFlow(): Flow<List<AppCrash>>

    fun getByIdAsFlow(id: Long): Flow<AppCrash?>

    suspend fun getAll(): List<AppCrash>

    suspend fun getById(id: Long): AppCrash?

    suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrash>

    suspend fun insert(appCrash: AppCrash): Long

    suspend fun update(item: AppCrash)

    suspend fun delete(item: AppCrash)

    suspend fun deleteAllByPackageName(appCrash: AppCrash)

    suspend fun clear()
}
