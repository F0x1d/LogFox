package com.f0x1d.logfox.feature.recordings.impl.data

import android.content.Context
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.di.MainDispatcher
import com.f0x1d.logfox.feature.database.api.data.LogRecordingDataSource
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.api.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import com.f0x1d.logfox.feature.recordings.impl.mapper.toDomainModel
import com.f0x1d.logfox.feature.recordings.impl.mapper.toEntity
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

internal class RecordingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logRecordingDataSource: LogRecordingDataSource,
    private val dateTimeFormatter: DateTimeFormatter,
    private val loggingRepository: LoggingRepository,
    private val logLineFormatterRepository: LogLineFormatterRepository,
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
                val batch = mutableListOf<String>()

                loggingRepository
                    .dumpLogs(
                        terminals.getValue(terminalSettingsRepository.selectedTerminalType().value),
                    )
                    .collect { line ->
                        batch += logLineFormatterRepository.format(
                            logLine = line,
                            formatDate = dateTimeFormatter::formatDate,
                            formatTime = dateTimeFormatter::formatTime,
                        )

                        if (batch.size >= BATCH_SIZE) {
                            out.write((batch.joinToString("\n") + "\n").encodeToByteArray())
                            batch.clear()
                        }
                    }

                if (batch.isNotEmpty()) {
                    out.write((batch.joinToString("\n") + "\n").encodeToByteArray())
                }
            }
        } catch (e: IOException) {
            withContext(mainDispatcher) {
                context.toast(Strings.error_saving_logs)
            }
            e.printStackTrace()
        } catch (_: TimeoutCancellationException) {
            // Collection of logs finished!
        }

        LogRecording(
            title = "${context.getString(
                Strings.record_file,
            )} ${logRecordingDataSource.count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let {
            it.copy(id = logRecordingDataSource.insert(it.toEntity()))
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
                logLineFormatterRepository.format(
                    logLine = it,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
            },
        )

        LogRecording(
            title = "${context.getString(
                Strings.record_file,
            )} ${logRecordingDataSource.count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let { logRecordingDataSource.insert(it.toEntity()) }
    }

    override suspend fun updateTitle(recordingId: Long, newTitle: String) {
        val recording = getById(recordingId) ?: return
        update(recording.copy(title = newTitle))
    }

    override fun getAllAsFlow(): Flow<List<LogRecording>> = logRecordingDataSource
        .getAllAsFlow()
        .map { list -> list.map { it.toDomainModel() } }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<LogRecording?> = logRecordingDataSource.getByIdAsFlow(id)
        .map { it?.toDomainModel() }
        .flowOn(ioDispatcher)

    override suspend fun getAll(): List<LogRecording> = withContext(ioDispatcher) {
        logRecordingDataSource.getAll().map { it.toDomainModel() }
    }

    override suspend fun getById(id: Long): LogRecording? = withContext(ioDispatcher) {
        logRecordingDataSource.getById(id)?.toDomainModel()
    }

    override suspend fun update(item: LogRecording) = withContext(ioDispatcher) {
        logRecordingDataSource.update(item.toEntity())
    }

    override suspend fun delete(item: LogRecording) = withContext(ioDispatcher) {
        item.deleteFile()
        logRecordingDataSource.delete(item.toEntity())
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach { it.deleteFile() }
        logRecordingDataSource.deleteAll()
    }

    private companion object {
        const val BATCH_SIZE = 100
    }
}
