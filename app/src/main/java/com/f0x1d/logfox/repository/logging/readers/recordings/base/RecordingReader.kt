package com.f0x1d.logfox.repository.logging.readers.recordings.base

import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.readers.base.LogsReader
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject

open class RecordingReader @Inject constructor(): LogsReader {

    private var recording = false
    var recordingTime = 0L
        private set
    private val recordingMutex = Mutex()

    private val recordedLines = mutableListOf<LogLine>()
    private val linesMutex = Mutex()

    var recordingFile: File? = null
        private set
    private val fileMutex = Mutex()

    suspend fun record(toFile: File) {
        recordingMutex.withLock {
            recording = true
            recordingTime = System.currentTimeMillis()
        }

        fileMutex.withLock {
            recordingFile = toFile
        }
    }

    suspend fun dumpLines() {
        val content = linesMutex.withLock {
            if (recordedLines.isEmpty())
                return@withLock ""

            val stringLogs = recordedLines.joinToString("\n") { it.original }
            recordedLines.clear()

            return@withLock stringLogs
        }

        if (content.isNotEmpty()) {
            fileMutex.withLock {
                recordingFile?.appendText(content + "\n")
            }
        }
    }

    suspend fun clearLines() = linesMutex.withLock {
        recordedLines.clear()
    }

    suspend fun deleteFile() = fileMutex.withLock {
        recordingFile?.delete()
    }

    suspend fun copyFileTo(file: File) = fileMutex.withLock {
        recordingFile?.copyTo(file)
    }

    suspend fun updateRecording(recording: Boolean) = recordingMutex.withLock {
        this.recording = recording
    }

    override suspend fun readLine(line: LogLine) {
        val recording = recordingMutex.withLock { recording }

        if (recording && shouldRecordLine(line)) linesMutex.withLock {
            recordedLines.add(line)
        }.also {
            if (recordedLines.size >= 1000)
                dumpLines()
        }
    }

    protected open suspend fun shouldRecordLine(line: LogLine) = true
}