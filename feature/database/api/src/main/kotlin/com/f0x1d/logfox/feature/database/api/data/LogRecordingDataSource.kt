package com.f0x1d.logfox.feature.database.api.data

import com.f0x1d.logfox.feature.database.api.entity.LogRecordingEntity
import kotlinx.coroutines.flow.Flow

interface LogRecordingDataSource {

    suspend fun getAll(cached: Boolean = false): List<LogRecordingEntity>

    fun getAllAsFlow(cached: Boolean = false): Flow<List<LogRecordingEntity>>

    suspend fun getById(id: Long): LogRecordingEntity?

    fun getByIdAsFlow(id: Long): Flow<LogRecordingEntity?>

    suspend fun count(cached: Boolean = false): Int

    suspend fun insert(logRecording: LogRecordingEntity): Long

    suspend fun update(logRecording: LogRecordingEntity)

    suspend fun delete(logRecording: LogRecordingEntity)

    suspend fun deleteAll()
}
