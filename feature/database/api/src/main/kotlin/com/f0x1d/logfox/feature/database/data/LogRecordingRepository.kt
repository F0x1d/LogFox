package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.model.LogRecording
import kotlinx.coroutines.flow.Flow

interface LogRecordingRepository {
    suspend fun getAll(): List<LogRecording>
    fun getAllAsFlow(): Flow<List<LogRecording>>
    suspend fun getById(id: Long): LogRecording?
    fun getByIdAsFlow(id: Long): Flow<LogRecording?>
    suspend fun count(): Int
    suspend fun insert(logRecording: LogRecording): Long
    suspend fun update(logRecording: LogRecording)
    suspend fun delete(logRecording: LogRecording)
    suspend fun deleteAll()
}
