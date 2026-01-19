package com.f0x1d.logfox.feature.recordings.api.data

import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.flow.Flow

interface RecordingsRepository {
    suspend fun saveAll(): LogRecording
    suspend fun createRecordingFrom(lines: List<LogLine>)

    suspend fun updateTitle(logRecording: LogRecording, newTitle: String)

    fun getAllAsFlow(): Flow<List<LogRecording>>

    fun getByIdAsFlow(id: Long): Flow<LogRecording?>

    suspend fun getAll(): List<LogRecording>

    suspend fun getById(id: Long): LogRecording?

    suspend fun update(item: LogRecording)

    suspend fun delete(item: LogRecording)

    suspend fun clear()
}
