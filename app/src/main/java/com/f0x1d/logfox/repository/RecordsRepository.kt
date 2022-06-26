package com.f0x1d.logfox.repository

import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.base.LoggingHelperRepository
import com.f0x1d.logfox.repository.readers.base.BaseReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordsRepository @Inject constructor(private val database: AppDatabase): LoggingHelperRepository() {

    val recordingsFlow = MutableStateFlow(emptyList<LogRecording>())
    val recordingStateFlow = MutableStateFlow(RecordingState.IDLE)

    override val readers = listOf(RecordingReader())

    private val recordedLines = mutableListOf<LogLine>()

    override suspend fun setup() {
        recordingsFlow.update {
            database.logRecordingDao().getAll()
        }
    }

    override fun stop() {
        end()
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
            recordingStateFlow.update { RecordingState.IDLE }
            if (recordedLines.isEmpty()) return@onAppScope

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

    fun deleteRecording(id: Long) {
        onAppScope {
            recordingsFlow.updateList {
                database.logRecordingDao().delete(id)
                removeAll { it.id == id }
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
    IDLE, RECORDING, PAUSED
}