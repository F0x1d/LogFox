package com.f0x1d.logfox.repository.logging

import android.content.Context
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.extensions.context.toast
import com.f0x1d.logfox.extensions.notifications.cancelRecordingNotification
import com.f0x1d.logfox.extensions.notifications.sendRecordingNotification
import com.f0x1d.logfox.extensions.notifications.sendRecordingPausedNotification
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.recordings.RecordingWithFiltersReader
import com.f0x1d.logfox.repository.logging.readers.recordings.RewritingRecordingReader
import com.f0x1d.logfox.utils.DateTimeFormatter
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.utils.terminal.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val dateTimeFormatter: DateTimeFormatter,
    private val recordingReader: RecordingWithFiltersReader,
    private val cacheRecordingReader: RewritingRecordingReader,
    private val appPreferences: AppPreferences,
    private val terminals: Array<Terminal>
): LoggingHelperItemsRepository<LogRecording>() {

    val recordingStateFlow = MutableStateFlow(RecordingState.IDLE)

    override val readers = listOf(
        recordingReader,
        cacheRecordingReader
    )

    private val recordingsDir = File("${context.filesDir.absolutePath}/recordings").apply {
        if (!exists()) mkdirs()
    }
    private val cacheRecordingsDir = File("${context.filesDir.absolutePath}/recordings/cache").apply {
        if (!exists()) mkdirs()
    }

    private var useLogsCache = appPreferences.useSessionCache
    private var savingCacheRecording = appPreferences.saveSessionCacheToRecordings

    override suspend fun setup() {
        super.setup()

        database.userFilterDao().getAllAsFlow()
            .flowOn(Dispatchers.IO)
            .onEach {
                recordingReader.updateFilters(it)
                cacheRecordingReader.updateFilters(it)
            }
            .launchIn(repositoryScope)

        // Deprecated
        File(recordingsDir, "all.log").delete()

        // Updated only in setup to avoid saving deleted file and etc.
        useLogsCache = appPreferences.useSessionCache
        savingCacheRecording = appPreferences.saveSessionCacheToRecordings

        if (useLogsCache) {
            cacheRecordingReader.record(
                File(
                    cacheRecordingsDir,
                    "${dateTimeFormatter.formatForExport(System.currentTimeMillis())}.log"
                )
            )

            repositoryScope.launch {
                while (isActive) {
                    delay(1000)
                    cacheRecordingReader.dumpLines()
                }
            }

            if (savingCacheRecording) database.logRecordingDao().insert(
                LogRecording(
                    title = "${context.getString(R.string.session_cache)} ${database.logRecordingDao().count(cached = true) + 1}",
                    dateAndTime = cacheRecordingReader.recordingTime,
                    file = cacheRecordingReader.recordingFile ?: return,
                    isCacheRecording = true
                )
            )
        }
    }

    override suspend fun stop() {
        if (useLogsCache) {
            cacheRecordingReader.stopRecording()
            if (!savingCacheRecording)
                cacheRecordingReader.recordingFile?.delete()
        }

        when (recordingStateFlow.value) {
            RecordingState.RECORDING,
            RecordingState.PAUSED -> end(recordingSaved = {}).join()

            RecordingState.SAVING -> repositoryScope.coroutineContext.job.join()

            else -> {}
        }

        super.stop()
    }

    fun saveAll(recordingSaved: (LogRecording) -> Unit = {}) = runOnRepoScope {
        val recordingTime = System.currentTimeMillis()
        val recordingFile = File(
            recordingsDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log"
        )

        val command = LoggingRepository.COMMAND + LoggingRepository.DUMP_FLAG
        val process = terminals[appPreferences.selectedTerminalIndex].execute(*command)

        try {
            FileOutputStream(recordingFile, true).use { out ->
                process?.output?.bufferedReader()?.useLines {
                    for (line in it) {
                        out.write((line + "\n").encodeToByteArray())
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                context.toast(R.string.error_saving_logs)
            }
            e.printStackTrace()
        }

        val logRecording = LogRecording(
            title = "${context.getString(R.string.record_file)} ${database.logRecordingDao().count() + 1}",
            dateAndTime = recordingTime,
            file = recordingFile
        ).let {
            it.copy(id = database.logRecordingDao().insert(it))
        }

        withContext(Dispatchers.Main) {
            recordingSaved(logRecording)
        }
    }

    fun createRecordingFrom(lines: List<com.f0x1d.logfox.model.LogLine>) = runOnRepoScope {
        val recordingTime = System.currentTimeMillis()

        val recordingFile = File(
            recordingsDir,
            "${dateTimeFormatter.formatForExport(recordingTime)}.log"
        )

        recordingFile.writeText(
            lines.joinToString("\n") {
                it.original
            }
        )

        database.logRecordingDao().insert(
            LogRecording(
                title = "${context.getString(R.string.record_file)} ${database.logRecordingDao().count() + 1}",
                dateAndTime = recordingTime,
                file = recordingFile
            )
        )
    }

    fun record() = runOnRepoScope {
        recordingStateFlow.update { RecordingState.RECORDING }

        recordingReader.record(
            File(
                recordingsDir,
                "${dateTimeFormatter.formatForExport(System.currentTimeMillis())}.log"
            )
        )

        context.sendRecordingNotification()
    }

    fun pause() = runOnRepoScope {
        recordingStateFlow.update { RecordingState.PAUSED }
        recordingReader.updateRecording(false)
        context.sendRecordingPausedNotification()
    }

    fun resume() = runOnRepoScope {
        recordingStateFlow.update { RecordingState.RECORDING }
        recordingReader.updateRecording(true)
        context.sendRecordingNotification()
    }

    fun end(recordingSaved: (LogRecording) -> Unit = {}) = onRepoScope {
        recordingStateFlow.update { RecordingState.SAVING }
        recordingReader.stopRecording()
        context.cancelRecordingNotification()

        val logRecording = LogRecording(
            title = "${context.getString(R.string.record_file)} ${database.logRecordingDao().count() + 1}",
            dateAndTime = recordingReader.recordingTime,
            file = recordingReader.recordingFile ?: return@onRepoScope
        ).let {
            it.copy(id = database.logRecordingDao().insert(it))
        }

        withContext(Dispatchers.Main) {
            recordingSaved(logRecording)
        }

        recordingStateFlow.update { RecordingState.IDLE }
    }

    fun updateTitle(logRecording: LogRecording, newTitle: String) = update(logRecording.copy(title = newTitle))

    fun clearCached() = runOnRepoScope {
        database.logRecordingDao().getAll(cached = true).forEach {
            if (it.file != cacheRecordingReader.recordingFile)
                it.deleteFile()
            else
                // To delete it later
                savingCacheRecording = false
        }
        database.logRecordingDao().deleteAll(deleteCacheRecordings = true)
    }

    override suspend fun updateInternal(item: LogRecording) = database.logRecordingDao().update(item)

    override suspend fun deleteInternal(item: LogRecording) {
        if (item.file != cacheRecordingReader.recordingFile)
            item.deleteFile()
        else
            // To delete it later
            savingCacheRecording = false
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
