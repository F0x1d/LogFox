package com.f0x1d.logfox.feature.recordings.api.data

import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.model.logline.LogLine

interface RecordingsRepository : DatabaseProxyRepository<LogRecording> {
    suspend fun saveAll(): LogRecording
    suspend fun createRecordingFrom(lines: List<LogLine>)

    suspend fun updateTitle(logRecording: LogRecording, newTitle: String)
}
