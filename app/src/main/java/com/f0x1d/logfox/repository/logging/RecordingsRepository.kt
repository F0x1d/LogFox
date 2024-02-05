package com.f0x1d.logfox.repository.logging

import android.content.Context
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.extensions.notifications.cancelRecordingNotification
import com.f0x1d.logfox.extensions.notifications.sendRecordingNotification
import com.f0x1d.logfox.extensions.notifications.sendRecordingPausedNotification
import com.f0x1d.logfox.extensions.onAppScope
import com.f0x1d.logfox.extensions.runOnAppScope
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.recordings.RecordingWithFiltersReader
import com.f0x1d.logfox.repository.logging.readers.recordings.base.RecordingReader
import com.f0x1d.logfox.utils.DateTimeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val dateTimeFormatter: DateTimeFormatter,
    private val recordingReader: RecordingWithFiltersReader,
    private val allRecordingReader: RecordingReader
): LoggingHelperItemsRepository<LogRecording>() {

    val recordingStateFlow = MutableStateFlow(RecordingState.IDLE)

    override val readers = listOf(
        recordingReader,
        allRecordingReader
    )

    private val recordingDir = File("${context.filesDir.absolutePath}/recordings").apply {
        if (!exists()) mkdirs()
    }
    private val allRecordingFile = File(recordingDir, "all.log")

    private var filtersJob: Job? = null

    override suspend fun setup() {
        filtersJob = onAppScope {
            database.userFilterDao().getAllAsFlow()
                .distinctUntilChanged()
                .flowOn(Dispatchers.IO)
                .collect {
                    recordingReader.updateFilters(it)
                }
        }

        allRecordingFile.delete()
        allRecordingReader.record(allRecordingFile)
    }

    override suspend fun stop() {
        if (recordingStateFlow.value != RecordingState.IDLE) {
            recordingReader.updateRecording(false)
            recordingReader.deleteFile()
        }

        recordingStateFlow.update { RecordingState.IDLE }
        recordingReader.clearLines()

        allRecordingReader.updateRecording(false)
        allRecordingReader.clearLines()

        filtersJob?.cancel()
    }

    fun saveAll(recordingSaved: (LogRecording) -> Unit = {}) = runOnAppScope {
        allRecordingReader.dumpLines()

        val recordingTime = System.currentTimeMillis()
        val recordingFile = File(
            recordingDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log"
        )

        allRecordingReader.copyFileTo(recordingFile)

        val logRecording = LogRecording(
            "${context.getString(R.string.recording)} ${database.logRecordingDao().count() + 1}",
            recordingTime,
            recordingFile.absolutePath
        ).let {
            it.copy(id = database.logRecordingDao().insert(it))
        }

        withContext(Dispatchers.Main) {
            recordingSaved(logRecording)
        }
    }

    fun createRecordingFrom(lines: List<LogLine>) = runOnAppScope {
        val recordingTime = System.currentTimeMillis()

        val recordingFile = File(
            recordingDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log"
        )

        recordingFile.writeText(
            lines.joinToString("\n") {
                it.original
            }
        )

        val title = "${context.getString(R.string.record_file)} ${database.logRecordingDao().count() + 1}"

        database.logRecordingDao().insert(
            LogRecording(
                title,
                recordingTime,
                recordingFile.absolutePath
            )
        )
    }

    fun record() = runOnAppScope {
        recordingStateFlow.update { RecordingState.RECORDING }

        recordingReader.record(
            File(
                recordingDir,
                "${dateTimeFormatter.formatForExport(System.currentTimeMillis())}.log"
            )
        )

        context.sendRecordingNotification()
    }

    fun pause() = runOnAppScope {
        recordingStateFlow.update { RecordingState.PAUSED }
        recordingReader.updateRecording(false)
        context.sendRecordingPausedNotification()
    }

    fun resume() = runOnAppScope {
        recordingStateFlow.update { RecordingState.RECORDING }
        recordingReader.updateRecording(true)
        context.sendRecordingNotification()
    }

    fun end(recordingSaved: (LogRecording) -> Unit = {}) = runOnAppScope {
        recordingStateFlow.update { RecordingState.SAVING }
        recordingReader.updateRecording(false)
        context.cancelRecordingNotification()

        recordingReader.dumpLines()

        val title = "${context.getString(R.string.record_file)} ${database.logRecordingDao().count() + 1}"

        val logRecording = LogRecording(
            title,
            recordingReader.recordingTime,
            recordingReader.recordingFile?.absolutePath ?: return@runOnAppScope
        ).let {
            it.copy(id = database.logRecordingDao().insert(it))
        }

        withContext(Dispatchers.Main) {
            recordingSaved(logRecording)
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
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, SAVING
}