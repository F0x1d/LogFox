package com.f0x1d.logfox.feature.recordings.impl.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import kotlinx.coroutines.flow.StateFlow

internal interface RecordingLocalDataSource {
    val recordingState: StateFlow<RecordingState>

    suspend fun processLogLine(logLine: LogLine)
    suspend fun record()
    suspend fun pause()
    suspend fun resume()
    suspend fun end(): LogRecording?

    suspend fun loggingStopped()
}
