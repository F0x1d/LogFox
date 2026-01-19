package com.f0x1d.logfox.feature.recordings.impl.data

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.database.data.LogRecordingRepository
import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.data.reader.RecordingReader
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RecordingLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logRecordingRepository: LogRecordingRepository,
    private val dateTimeFormatter: DateTimeFormatter,
    override val reader: RecordingReader,
    private val notificationsLocalDataSource: RecordingNotificationsLocalDataSource,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : RecordingLocalDataSource {

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

        notificationsLocalDataSource.sendRecordingNotification()
    }

    override suspend fun pause() = withContext(ioDispatcher) {
        state.update { RecordingState.PAUSED }
        reader.updateRecording(false)
        notificationsLocalDataSource.sendRecordingPausedNotification()
    }

    override suspend fun resume() = withContext(ioDispatcher) {
        state.update { RecordingState.RECORDING }
        reader.updateRecording(true)
        notificationsLocalDataSource.sendRecordingNotification()
    }

    override suspend fun end(): LogRecording? = withContext(ioDispatcher) {
        state.update { RecordingState.SAVING }
        reader.stopRecording()
        notificationsLocalDataSource.cancelRecordingNotification()

        val logRecording = LogRecording(
            title = "${context.getString(Strings.record_file)} ${logRecordingRepository.count() + 1}",
            dateAndTime = reader.recordingTime,
            file = reader.recordingFile ?: return@withContext null,
        ).let {
            it.copy(id = logRecordingRepository.insert(it))
        }

        state.update { RecordingState.IDLE }

        return@withContext logRecording
    }

    override suspend fun loggingStopped(): Unit = withContext(ioDispatcher) {
        when (state.value) {
            RecordingState.RECORDING,
            RecordingState.PAUSED -> { end() }

            else -> Unit
        }
    }
}
