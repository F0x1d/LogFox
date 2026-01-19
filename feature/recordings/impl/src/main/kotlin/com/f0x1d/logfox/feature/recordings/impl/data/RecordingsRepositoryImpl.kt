package com.f0x1d.logfox.feature.recordings.impl.data

import android.content.Context
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.di.MainDispatcher
import com.f0x1d.logfox.feature.database.data.LogRecordingRepository
import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

internal class RecordingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logRecordingRepository: LogRecordingRepository,
    private val dateTimeFormatter: DateTimeFormatter,
    private val loggingRepository: LoggingRepository,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    private val terminalSettingsRepository: TerminalSettingsRepository,
    private val terminals: Map<TerminalType, @JvmSuppressWildcards Terminal>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : RecordingsRepository {

    private val recordingsDir = File("${context.filesDir.absolutePath}/recordings").apply {
        if (!exists()) mkdirs()
    }

    override suspend fun saveAll(): LogRecording = withContext(ioDispatcher) {
        val recordingTime = System.currentTimeMillis()
        val recordingFile = File(
            recordingsDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log",
        )

        try {
            FileOutputStream(recordingFile, true).use { out ->
                loggingRepository
                    .dumpLogs(
                        terminals.getValue(terminalSettingsRepository.selectedTerminalType().value),
                    )
                    .collect { line ->
                        // It is on IO!
                        val original = formatLogLineUseCase(
                            logLine = line,
                            formatDate = dateTimeFormatter::formatDate,
                            formatTime = dateTimeFormatter::formatTime,
                        )
                        out.write((original + "\n").encodeToByteArray())
                    }
            }
        } catch (e: IOException) {
            withContext(mainDispatcher) {
                context.toast(Strings.error_saving_logs)
            }
            e.printStackTrace()
        }

        LogRecording(
            title = "${context.getString(
                Strings.record_file,
            )} ${logRecordingRepository.count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let {
            it.copy(id = logRecordingRepository.insert(it))
        }
    }

    override suspend fun createRecordingFrom(lines: List<LogLine>): Unit = withContext(ioDispatcher) {
        val recordingTime = System.currentTimeMillis()

        val recordingFile = File(
            recordingsDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log",
        )

        recordingFile.writeText(
            lines.joinToString("\n") {
                formatLogLineUseCase(
                    logLine = it,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
            },
        )

        LogRecording(
            title = "${context.getString(
                Strings.record_file,
            )} ${logRecordingRepository.count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let { logRecordingRepository.insert(it) }
    }

    override suspend fun updateTitle(logRecording: LogRecording, newTitle: String) = update(
        logRecording.copy(
            title = newTitle,
        ),
    )

    override fun getAllAsFlow(): Flow<List<LogRecording>> = logRecordingRepository
        .getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<LogRecording?> = logRecordingRepository.getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<LogRecording> = withContext(ioDispatcher) {
        logRecordingRepository.getAll()
    }

    override suspend fun getById(id: Long): LogRecording? = withContext(ioDispatcher) {
        logRecordingRepository.getById(id)
    }

    override suspend fun update(item: LogRecording) = withContext(ioDispatcher) {
        logRecordingRepository.update(item)
    }

    override suspend fun delete(item: LogRecording) = withContext(ioDispatcher) {
        item.deleteFile()
        logRecordingRepository.delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach { it.deleteFile() }
        logRecordingRepository.deleteAll()
    }
}
