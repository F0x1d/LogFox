package com.f0x1d.logfox.feature.recordings.core.controller

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.core.controller.reader.RecordingWithFiltersReader
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface RecordingController {
    val recordingState: StateFlow<RecordingState>
    val reader: suspend (LogLine) -> Unit

    suspend fun record()
    suspend fun pause()
    suspend fun resume()
    suspend fun end(): LogRecording?

    suspend fun filtersUpdated(filters: List<UserFilter>)
    suspend fun loggingStopped()
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, SAVING
}

@Singleton
internal class RecordingControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val dateTimeFormatter: DateTimeFormatter,
    override val reader: RecordingWithFiltersReader,
    private val notificationController: RecordingNotificationController,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : RecordingController {

    private val recordingsDir = File("${context.filesDir.absolutePath}/recordings").apply {
        if (!exists()) mkdirs()
    }
    private val cacheRecordingsDir = File("${context.filesDir.absolutePath}/recordings/cache").apply {
        if (!exists()) mkdirs()
    }

    private val state = MutableStateFlow(RecordingState.IDLE)
    override val recordingState: StateFlow<RecordingState> = state

    override suspend fun record() = withContext(ioDispatcher) {
        state.update { RecordingState.RECORDING }

        reader.record(
            File(
                recordingsDir,
                "${dateTimeFormatter.formatForExport(System.currentTimeMillis())}.log"
            )
        )

        notificationController.sendRecordingNotification()
    }

    override suspend fun pause() = withContext(ioDispatcher) {
        state.update { RecordingState.PAUSED }
        reader.updateRecording(false)
        notificationController.sendRecordingPausedNotification()
    }

    override suspend fun resume() = withContext(ioDispatcher) {
        state.update { RecordingState.RECORDING }
        reader.updateRecording(true)
        notificationController.sendRecordingNotification()
    }

    override suspend fun end(): LogRecording? = withContext(ioDispatcher) {
        state.update { RecordingState.SAVING }
        reader.stopRecording()
        notificationController.cancelRecordingNotification()

        val logRecording = LogRecording(
            title = "${context.getString(Strings.record_file)} ${database.logRecordingDao().count() + 1}",
            dateAndTime = reader.recordingTime,
            file = reader.recordingFile ?: return@withContext null,
        ).let {
            it.copy(id = database.logRecordingDao().insert(it))
        }

        state.update { RecordingState.IDLE }

        return@withContext logRecording
    }

    override suspend fun filtersUpdated(filters: List<UserFilter>) = withContext(ioDispatcher) {
        reader.updateFilters(filters)
    }

    override suspend fun loggingStopped(): Unit = withContext(ioDispatcher) {
        when (state.value) {
            RecordingState.RECORDING,
            RecordingState.PAUSED -> { end() }

            else -> Unit
        }
    }
}
