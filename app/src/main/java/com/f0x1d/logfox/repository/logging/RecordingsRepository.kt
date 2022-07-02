package com.f0x1d.logfox.repository.logging

import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.base.LoggingHelperRepository
import com.f0x1d.logfox.repository.logging.readers.base.BaseReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingsRepository @Inject constructor(private val database: AppDatabase): LoggingHelperRepository() {

    val recordingsFlow = MutableStateFlow(emptyList<LogRecording>())
    val recordingStateFlow = MutableStateFlow(RecordingState.IDLE)

    override val readers = listOf(RecordingReader())

    private val recordedLines = mutableListOf<LogLine>()

    override suspend fun setup() {
        recordingsFlow.update {
            database.logRecordingDao().getAll()
        }
    }

    override suspend fun stop() {
        recordingStateFlow.update { RecordingState.IDLE }
        recordedLines.clear()
    }

    fun record() {
        onAppScope {
            recordingStateFlow.update { RecordingState.RECORDING }
        }
    }

    fun pause() {
        onAppScope {
            recordingStateFlow.update { RecordingState.PAUSED }
        }
    }

    fun end(recordingSaved: (LogRecording) -> Unit = {}) {
        onAppScope {
            if (recordedLines.isEmpty()) return@onAppScope
            recordingStateFlow.update { RecordingState.SAVING }

            recordingsFlow.updateList {
                val logRecording = LogRecording(recordedLines.first().dateAndTime, recordedLines.joinToString("\n") { it.original }).run {
                    copy(id = database.logRecordingDao().insert(this))
                }

                add(0, logRecording)

                withContext(Dispatchers.Main) {
                    recordingSaved.invoke(logRecording)
                }
            }

            recordedLines.clear()
            recordingStateFlow.update { RecordingState.IDLE }
        }
    }

    fun deleteRecording(logRecording: LogRecording) {
        onAppScope {
            recordingsFlow.updateList {
                remove(logRecording)
                database.logRecordingDao().delete(logRecording)
            }
        }
    }

    fun clearRecordings() {
        onAppScope {
            recordingsFlow.update {
                database.logRecordingDao().deleteAll()
                emptyList()
            }
        }
    }

    inner class RecordingReader: BaseReader {
        override suspend fun readLine(line: LogLine) {
            if (recordingStateFlow.value == RecordingState.RECORDING) {
                recordedLines.add(line)
            }
        }
    }
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, SAVING
}