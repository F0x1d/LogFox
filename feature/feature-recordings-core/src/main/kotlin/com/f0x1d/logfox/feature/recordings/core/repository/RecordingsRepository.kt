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
                        out.write((line.original + "\n").encodeToByteArray())
                    }
            }
        } catch (e: IOException) {
            withContext(mainDispatcher) {
                context.toast(Strings.error_saving_logs)
            }
            e.printStackTrace()
        }

        LogRecording(
            title = "${context.getString(Strings.record_file)} ${database.logRecordingDao().count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let {
            it.copy(id = database.logRecordingDao().insert(it))
        }
    }

    override suspend fun createRecordingFrom(lines: List<LogLine>) {
        val recordingTime = System.currentTimeMillis()

        val recordingFile = File(
            recordingsDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log",
        )

        recordingFile.writeText(
            lines.joinToString("\n") {
                it.original
            },
        )

        LogRecording(
            title = "${context.getString(Strings.record_file)} ${database.logRecordingDao().count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile,
        ).let { database.logRecordingDao().insert(it) }
    }

    override suspend fun updateTitle(logRecording: LogRecording, newTitle: String) = update(
        logRecording.copy(
            title = newTitle,
        ),
    )

    override fun getAllAsFlow(): Flow<List<LogRecording>> =
        database.logRecordingDao().getAllAsFlow()

    override fun getByIdAsFlow(id: Long): Flow<LogRecording?> =
        database.logRecordingDao().getByIdAsFlow(id)

    override suspend fun getAll(): List<LogRecording> = withContext(ioDispatcher) {
        database.logRecordingDao().getAll()
    }

    override suspend fun getById(id: Long): LogRecording? = withContext(ioDispatcher) {
        database.logRecordingDao().getById(id)
    }

    override suspend fun update(item: LogRecording) = withContext(ioDispatcher) {
        database.logRecordingDao().update(item)
    }

    override suspend fun delete(item: LogRecording) = withContext(ioDispatcher) {
        item.deleteFile()
        database.logRecordingDao().delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach { it.deleteFile() }
        database.logRecordingDao().deleteAll()
    }
}
