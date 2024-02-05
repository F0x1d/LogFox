package com.f0x1d.logfox.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.di.viewmodel.FileUri
import com.f0x1d.logfox.extensions.logline.filterAndSearch
import com.f0x1d.logfox.extensions.readFileContentsAsFlow
import com.f0x1d.logfox.extensions.readFileName
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.utils.DateTimeFormatter
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    @FileUri val fileUri: Uri?,
    private val database: AppDatabase,
    private val loggingRepository: LoggingRepository,
    private val recordingsRepository: RecordingsRepository,
    val dateTimeFormatter: DateTimeFormatter,
    val appPreferences: AppPreferences,
    application: Application
): BaseViewModel(application) {

    val query = MutableStateFlow<String?>(null)
    val queryAndFilters = query.combine(database.userFilterDao().getAllAsFlow()) { query, filters ->
        query to filters.filter { it.enabled }
    }.flowOn(Dispatchers.IO)

    val paused = MutableStateFlow(false)

    val viewingFile = fileUri != null
    val viewingFileName = fileUri?.readFileName(ctx)

    val selectedItems = MutableStateFlow(emptyList<LogLine>())

    val selectedItemsContent get() = selectedItems.value.joinToString("\n") { it.original }

    val logs = combine(
        fileUri?.readFileContentsAsFlow(ctx) ?: loggingRepository.logsFlow,
        database.userFilterDao().getAllAsFlow(),
        query,
        if (!viewingFile) paused else flowOf(false)
    ) { logs, filters, query, paused ->
        LogsData(logs, filters, query, paused)
    }.scan(null as LogsData?) { accumulator, data ->
        when {
            !data.paused -> data

            data.query != accumulator?.query || data.filters != accumulator?.filters -> data.copy(
                logs = accumulator?.logs ?: emptyList()
            )

            else -> data.copy(logs = accumulator.logs, passing = false)
        }
    }.filter {
        it?.passing == true
    }.mapNotNull {
        it?.run {
            logs.filterAndSearch(filters, query)
        }
    }.flowOn(
        Dispatchers.IO
    ).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    val serviceRunningData = loggingRepository.serviceRunningFlow.asLiveData()

    val resumeLoggingWithBottomTouch get() = appPreferences.resumeLoggingWithBottomTouch

    fun selectLine(logLine: LogLine, selected: Boolean) = selectedItems.updateList {
        if (selected) add(
            logLine
        ) else remove(
            logLine
        )
    }

    fun selectAll() {
        if (selectedItems.value == logs.value) selectedItems.update {
            emptyList()
        } else selectedItems.update {
            logs.value
        }
    }

    fun selectedToRecording() = recordingsRepository.createRecordingFrom(selectedItems.value)

    fun exportSelectedLogsTo(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.use {
            it.write(
                selectedItems.value.joinToString("\n") { line ->
                    line.original
                }.encodeToByteArray()
            )
        }
    }

    fun query(query: String?) = this.query.update { query }

    fun clearLogs() = loggingRepository.clearLogs().also {
        selectedItems.update { emptyList() }
    }

    fun restartLogging() = loggingRepository.restartLogging()

    fun switchState() = if (!paused.value)
        pause()
    else
        resume()

    fun pause() = paused.update { true }
    fun resume() = paused.update { false }

    private data class LogsData(
        val logs: List<LogLine>,
        val filters: List<UserFilter>,
        val query: String?,
        val paused: Boolean,
        val passing: Boolean = true
    )
}