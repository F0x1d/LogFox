package com.f0x1d.logfox.feature.recordings.core.repository

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.di.MainDispatcher
import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.context.toast
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.core.repository.LoggingRepository
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.terminals.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

interface RecordingsRepository : DatabaseProxyRepository<LogRecording> {
    suspend fun saveAll(): LogRecording
    suspend fun createRecordingFrom(lines: List<LogLine>)

    suspend fun updateTitle(logRecording: LogRecording, newTitle: String)
}

internal class RecordingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val dateTimeFormatter: DateTimeFormatter,
    private val loggingRepository: LoggingRepository,
    private val appPreferences: AppPreferences,
    private val terminals: Array<Terminal>,
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
                loggingRepository.dumpLogs(terminals[appPreferences.selectedTerminalIndex])
                    .collect { line ->
                        // It is on IO!
                        val original = appPreferences.originalOf(
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
            title = "${context.getString(Strings.record_file)} ${database.logRecordings().count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let {
            it.copy(id = database.logRecordings().insert(it))
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
                appPreferences.originalOf(
                    logLine = it,
                    formatDate = dateTimeFormatter::formatDate,
                    formatTime = dateTimeFormatter::formatTime,
                )
            },
        )

        LogRecording(
            title = "${context.getString(Strings.record_file)} ${database.logRecordings().count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let { database.logRecordings().insert(it) }
    }

    override suspend fun updateTitle(logRecording: LogRecording, newTitle: String) = update(
        logRecording.copy(
            title = newTitle,
        ),
    )

    override fun getAllAsFlow(): Flow<List<LogRecording>> =
        database.logRecordings().getAllAsFlow().flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<LogRecording?> =
        database.logRecordings().getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<LogRecording> = withContext(ioDispatcher) {
        database.logRecordings().getAll()
    }

    override suspend fun getById(id: Long): LogRecording? = withContext(ioDispatcher) {
        database.logRecordings().getById(id)
    }

    override suspend fun update(item: LogRecording) = withContext(ioDispatcher) {
        database.logRecordings().update(item)
    }

    override suspend fun delete(item: LogRecording) = withContext(ioDispatcher) {
        item.deleteFile()
        database.logRecordings().delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach { it.deleteFile() }
        database.logRecordings().deleteAll()
    }
}
