package com.f0x1d.feature.logging.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.feature.logging.di.FileUri
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.core.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.core.store.LoggingStore
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
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
    private val loggingStore: LoggingStore,
    //private val recordingsRepository: RecordingsRepository,
    val appPreferences: AppPreferences,
    val dateTimeFormatter: DateTimeFormatter,
    application: Application
): BaseViewModel(application) {

    val query = MutableStateFlow<String?>(null)
    val queryAndFilters = query.combine(
        database.userFilterDao().getAllAsFlow()
    ) { query, filters ->
        query to filters.filter {
            it.enabled
        }
    }.flowOn(Dispatchers.IO)

    val paused = MutableStateFlow(false)

    val viewingFile = fileUri != null
    val viewingFileName = fileUri?.readFileName(ctx)

    val selectedItems = MutableStateFlow(emptyList<LogLine>())

    val selectedItemsContent get() = selectedItems.value.joinToString("\n") { it.original }

    val logs = combine(
        fileUri?.readFileContentsAsFlow(
            context = ctx,
            logsDisplayLimit = appPreferences.logsDisplayLimit,
        ) ?: loggingStore.logs,
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
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    val resumeLoggingWithBottomTouch get() = appPreferences.resumeLoggingWithBottomTouch

    // TODO
    fun selectLine(logLine: LogLine, selected: Boolean) = Unit /*selectedItems.updateList {
        if (selected) add(
            logLine
        ) else remove(
            logLine
        )
    }*/

    fun selectAll() {
        if (selectedItems.value == logs?.value) selectedItems.update {
            emptyList()
        } else selectedItems.update {
            logs?.value ?: emptyList()
        }
    }

    // TODO
    //fun selectedToRecording() = recordingsRepository.createRecordingFrom(selectedItems.value)

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
        val passing: Boolean = true,
    )
}
