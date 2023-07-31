package com.f0x1d.logfox.repository.logging

import android.content.Context
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.extensions.exportFormatted
import com.f0x1d.logfox.extensions.notifications.removeRecordingNotification
import com.f0x1d.logfox.extensions.notifications.sendRecordingNotification
import com.f0x1d.logfox.extensions.notifications.sendRecordingPausedNotification
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.base.LogsReader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
): LoggingHelperItemsRepository<LogRecording>() {

    val recordingStateFlow = MutableStateFlow(RecordingState.IDLE)

    override val readers = listOf(RecordingReader())

    private val recordedLines = mutableListOf<LogLine>()

    private val linesMutex = Mutex()
    private val fileMutex = Mutex()

    private var recordingDir: File? = null

    private var recordingTime = 0L
    private var recordingFile: File? = null
    private var recordingJob: Job? = null

    override suspend fun setup() {
        recordingDir = File("${context.filesDir.absolutePath}/recordings").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun stop() {
        if (recordingStateFlow.value != RecordingState.IDLE) {
            recordingJob?.cancel()
            fileMutex.withLock {
                recordingFile?.delete()
            }
        }

        recordingStateFlow.update { RecordingState.IDLE }
        linesMutex.withLock {
            recordedLines.clear()
        }
    }

    fun record() = runOnAppScope {
        recordingStateFlow.update { RecordingState.RECORDING }

        recordingTime = System.currentTimeMillis()
        fileMutex.withLock {
            recordingFile = File(recordingDir, "${recordingTime.exportFormatted}.txt").apply {
                createNewFile()
            }
        }

        recordingJob = onAppScope {
            while (isActive) {
                delay(1000)

                writeLogsToFile()
            }
        }

        context.sendRecordingNotification()
    }

    fun pause() {
        recordingStateFlow.update { RecordingState.PAUSED }
        context.sendRecordingPausedNotification()
    }

    fun resume() {
        recordingStateFlow.update { RecordingState.RECORDING }
        context.sendRecordingNotification()
    }

    fun end(recordingSaved: (LogRecording) -> Unit = {}) = runOnAppScope {
        recordingStateFlow.update { RecordingState.SAVING }
        context.removeRecordingNotification()

        recordingJob?.cancel()

        writeLogsToFile()

        val title = "${context.getString(R.string.record_file)} ${database.logRecordingDao().count() + 1}"

        val logRecording = LogRecording(title, recordingTime, recordingFile!!.absolutePath).run {
            copy(id = database.logRecordingDao().insert(this))
        }

        withContext(Dispatchers.Main) {
            recordingSaved.invoke(logRecording)
        }

        recordingStateFlow.update { RecordingState.IDLE }
    }

    fun updateTitle(logRecording: LogRecording, newTitle: String) = update(logRecording.copy(title = newTitle))

    override suspend fun updateInternal(item: LogRecording) = database.logRecordingDao().update(item)

    override suspend fun deleteInternal(item: LogRecording) {
        item.deleteFile()
        database.logRecordingDao().delete(item)
    }

    override suspend fun clearInternal() {
        database.logRecordingDao().getAll().forEach {
            it.deleteFile()
        }
        database.logRecordingDao().deleteAll()
    }

    private suspend fun writeLogsToFile() {
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

    inner class RecordingReader: LogsReader {
        override suspend fun readLine(line: LogLine) {
            if (recordingStateFlow.value == RecordingState.RECORDING) {
                linesMutex.withLock {
                    recordedLines.add(line)
                }
            }
        }
    }
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, SAVING
}