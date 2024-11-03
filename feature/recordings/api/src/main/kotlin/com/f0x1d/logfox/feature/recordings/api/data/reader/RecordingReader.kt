package com.f0x1d.logfox.feature.recordings.api.data.reader

import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject

open class RecordingReader @Inject constructor(
    private val appPreferences: AppPreferences,
    private val dateTimeFormatter: DateTimeFormatter,
): suspend (LogLine) -> Unit {

    private var recording = false
    var recordingTime = 0L
        private set
    private val recordingMutex = Mutex()

    private val recordedLines = mutableListOf<LogLine>()
    private val linesMutex = Mutex()

    var recordingFile: File? = null
        private set
    protected val fileMutex = Mutex()

    suspend fun record(toFile: File) {
        recordingMutex.withLock {
            recording = true
            recordingTime = System.currentTimeMillis()
        }

        fileMutex.withLock {
            recordingFile = toFile
        }
    }

    suspend fun stopRecording() {
        updateRecording(false)
        dumpLines()
    }

    suspend fun dumpLines() {
        val content = linesMutex.withLock {
            if (recordedLines.isEmpty())
                return@withLock ""

            val stringLogs = recordedLines.joinToString("\n") {
                appPreferences.originalOf(
                    logLine = it,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
            }
            recordedLines.clear()

            return@withLock stringLogs
        }

        if (content.isNotEmpty()) {
            writeToFile(content)
        }
    }

    suspend fun updateRecording(recording: Boolean) = recordingMutex.withLock {
        this.recording = recording
    }

    suspend fun copyFileTo(file: File) = fileMutex.withLock {
        recordingFile?.copyTo(file)
    }

    override suspend fun invoke(line: LogLine) {
        val recording = recordingMutex.withLock { recording }

        if (recording && shouldRecordLine(line)) {
            linesMutex.withLock {
                recordedLines.add(line)
            }

            if (recordedLines.size >= appPreferences.logsDisplayLimit)
                dumpLines()
        }
    }

    protected open suspend fun writeToFile(content: String) = fileMutex.withLock {
        recordingFile?.appendText(content + "\n")
    }

    protected open suspend fun shouldRecordLine(line: LogLine) = true
}
