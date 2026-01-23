package com.f0x1d.logfox.feature.recordings.impl.data

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.database.data.LogRecordingRepository
import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RecordingLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logRecordingRepository: LogRecordingRepository,
    private val dateTimeFormatter: DateTimeFormatter,
    private val logLineFormatterRepository: LogLineFormatterRepository,
    private val logsSettingsRepository: LogsSettingsRepository,
    private val notificationsLocalDataSource: RecordingNotificationsLocalDataSource,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : RecordingLocalDataSource {

    private val recordingsDir = File("${context.filesDir.absolutePath}/recordings").apply {
        if (!exists()) mkdirs()
    }

    private val state = MutableStateFlow(RecordingState.IDLE)
    override val recordingState: StateFlow<RecordingState> = state

    private var recordingTime = 0L
    private var recordingFile: File? = null
    private val fileMutex = Mutex()

    private val recordedLines = mutableListOf<LogLine>()
    private val linesMutex = Mutex()

    override suspend fun record() = withContext(ioDispatcher) {
        linesMutex.withLock {
            recordedLines.clear()
        }

        recordingTime = System.currentTimeMillis()

        fileMutex.withLock {
            recordingFile = File(
                recordingsDir,
                "${dateTimeFormatter.formatForExport(recordingTime)}.log",
            )
        }

        state.update { RecordingState.RECORDING }
        notificationsLocalDataSource.sendRecordingNotification()
    }

    override suspend fun pause() = withContext(ioDispatcher) {
        state.update { RecordingState.PAUSED }
        notificationsLocalDataSource.sendRecordingPausedNotification()
    }

    override suspend fun resume() = withContext(ioDispatcher) {
        state.update { RecordingState.RECORDING }
        notificationsLocalDataSource.sendRecordingNotification()
    }

    override suspend fun end(): LogRecording? = withContext(ioDispatcher) {
        state.update { RecordingState.SAVING }
        dumpLines()
        notificationsLocalDataSource.cancelRecordingNotification()

        val file = recordingFile ?: return@withContext null

        val logRecording = LogRecording(
            title = "${context.getString(Strings.record_file)} ${logRecordingRepository.count() + 1}",
            dateAndTime = recordingTime,
            file = file,
        ).let {
            it.copy(id = logRecordingRepository.insert(it))
        }

        state.update { RecordingState.IDLE }

        return@withContext logRecording
    }

    override suspend fun loggingStopped(): Unit = withContext(ioDispatcher) {
        when (state.value) {
            RecordingState.RECORDING,
            RecordingState.PAUSED,
            -> end()

            else -> Unit
        }
    }

    override suspend fun processLogLine(logLine: LogLine) {
        if (state.value != RecordingState.RECORDING) return

        val shouldDump = linesMutex.withLock {
            recordedLines.add(logLine)
            recordedLines.size >= logsSettingsRepository.logsDisplayLimit().value
        }

        if (shouldDump) {
            dumpLines()
        }
    }

    private suspend fun dumpLines() {
        val content = linesMutex.withLock {
            if (recordedLines.isEmpty()) return@withLock ""

            val formatted = recordedLines.joinToString("\n") {
                logLineFormatterRepository.format(
                    logLine = it,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
            }
            recordedLines.clear()

            formatted
        }

        if (content.isNotEmpty()) {
            fileMutex.withLock {
                recordingFile?.appendText(content + "\n")
            }
        }
    }
}
