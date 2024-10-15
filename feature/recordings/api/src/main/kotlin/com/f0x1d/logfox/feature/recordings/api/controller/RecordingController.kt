package com.f0x1d.logfox.feature.recordings.api.controller

import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.flow.StateFlow

interface RecordingController {
    val recordingState: StateFlow<RecordingState>
    val reader: suspend (LogLine) -> Unit

    suspend fun record()
    suspend fun pause()
    suspend fun resume()
    suspend fun end(): LogRecording?

    suspend fun loggingStopped()
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, SAVING
}
