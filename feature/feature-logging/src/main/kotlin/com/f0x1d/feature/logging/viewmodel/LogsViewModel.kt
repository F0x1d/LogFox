package com.f0x1d.feature.logging.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.feature.logging.di.FileUri
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.core.repository.FiltersRepository
import com.f0x1d.logfox.feature.logging.core.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.core.store.LoggingStore
import com.f0x1d.logfox.feature.recordings.core.repository.RecordingsRepository
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    @FileUri val fileUri: Uri?,
    private val loggingStore: LoggingStore,
    private val filtersRepository: FiltersRepository,
    private val recordingsRepository: RecordingsRepository,
    val appPreferences: AppPreferences,
    val dateTimeFormatter: DateTimeFormatter,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val query = MutableStateFlow<String?>(null)
    val queryAndFilters = query.combine(
        filtersRepository.getAllEnabledAsFlow(),
    ) { query, filters -> query to filters }

    val paused = MutableStateFlow(false)

    val viewingFile = fileUri != null
    val viewingFileName = fileUri?.readFileName(ctx)

    val selectedItems = MutableStateFlow(emptySet<LogLine>())

    val selectedItemsContent get() = selectedItems.value.joinToString("\n") { line ->
        appPreferences.originalOf(
            logLine = line,
            formatDate = dateTimeFormatter::formatDate,
            formatTime = dateTimeFormatter::formatTime,
        )
    }

    val logs = combine(
        fileUri?.readFileContentsAsFlow(
            context = ctx,
            logsDisplayLimit = appPreferences.logsDisplayLimit,
        ) ?: loggingStore.logs,
        filtersRepository.getAllEnabledAsFlow(),
        query,
        if (!viewingFile) paused else flowOf(false)
    ) { logs, filters, query, paused ->
        LogsData(logs, filters, query, paused)
    }.scan(LogsData()) { accumulator, data ->
        when {
            !data.paused -> data

            data.query != accumulator.query
                    || data.filters != accumulator.filters
                    || data.logs.isNotEmpty() // Logs were cleared
            -> data.copy(
                logs = accumulator.logs,
            )

            else -> data.copy(
                logs = accumulator.logs,
                passing = false,
            )
        }
    }.filter { data ->
        data.passing
    }.mapNotNull { data ->
        data.logs.filterAndSearch(
            filters = data.filters,
            query = data.query,
        )
    }.flowOn(
        ioDispatcher,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    val resumeLoggingWithBottomTouch get() = appPreferences.resumeLoggingWithBottomTouch

    fun selectLine(logLine: LogLine, selected: Boolean) = selectedItems.updateSet {
        if (selected) add(
            logLine
        ) else remove(
            logLine
        )
    }

    fun selectAll() {
        if (selectedItems.value == logs.value) selectedItems.update {
            emptySet()
        } else selectedItems.update {
            logs.value.toSet()
        }
    }

    fun clearSelection() = selectedItems.update { emptySet() }

    fun selectedToRecording() = viewModelScope.launch {
        recordingsRepository.createRecordingFrom(
            lines = withContext(defaultDispatcher) {
                selectedItems.value.sortedBy { it.dateAndTime }
            },
        )
    }

    fun exportSelectedLogsTo(uri: Uri) = launchCatching(ioDispatcher) {
        ctx.contentResolver.openOutputStream(uri)?.use {
            it.write(
                selectedItems.value.joinToString("\n") { line ->
                    appPreferences.originalOf(
                        logLine = line,
                        formatDate = dateTimeFormatter::formatDate,
                        formatTime = dateTimeFormatter::formatTime,
                    )
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

    private fun MutableStateFlow<Set<LogLine>>.updateSet(block: MutableSet<LogLine>.() -> Unit) = update {
        it.toMutableSet().apply(block).toSet()
    }

    private data class LogsData(
        val logs: List<LogLine> = emptyList(),
        val filters: List<UserFilter> = emptyList(),
        val query: String? = null,
        val paused: Boolean = false,
        val passing: Boolean = true,
    )
}
