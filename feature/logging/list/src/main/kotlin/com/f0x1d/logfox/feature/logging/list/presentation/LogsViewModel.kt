package com.f0x1d.logfox.feature.logging.list.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.logging.api.data.LogsDataSource
import com.f0x1d.logfox.feature.logging.api.data.QueryDataSource
import com.f0x1d.logfox.feature.logging.api.data.SelectedLogLinesDataSource
import com.f0x1d.logfox.feature.logging.api.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.list.di.FileUri
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    @FileUri val fileUri: Uri?,
    private val logsDataSource: LogsDataSource,
    private val queryDataSource: QueryDataSource,
    private val selectedLogLinesDataSource: SelectedLogLinesDataSource,
    private val filtersRepository: FiltersRepository,
    private val recordingsRepository: RecordingsRepository,
    private val appPreferences: AppPreferences,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    dateTimeFormatter: DateTimeFormatter,
    application: Application,
) : BaseViewModel<LogsState, LogsAction>(
    initialStateProvider = { LogsState() },
    application = application,
), DateTimeFormatter by dateTimeFormatter {
    val viewingFile = fileUri != null
    val viewingFileName = fileUri?.readFileName(ctx)

    val resumeLoggingWithBottomTouch get() = appPreferences.resumeLoggingWithBottomTouch
    val logsTextSize get() = appPreferences.logsTextSize.toFloat()
    val logsExpanded get() = appPreferences.logsExpanded
    val logsFormat get() = appPreferences.showLogValues

    val selectedItemsContent get() = currentState
        .selectedItems
        .sortedBy { it.dateAndTime }
        .joinToString("\n") {
            originalOf(it)
        }

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            state
                .map { it.selectedItems }
                .distinctUntilChanged()
                .onEach { lines ->
                    selectedLogLinesDataSource.updateSelectedLines(
                        selectedLines = lines.sortedBy { it.dateAndTime },
                    )
                }
                .launchIn(this)

            combine(
                fileUri?.readFileContentsAsFlow(
                    context = ctx,
                    logsDisplayLimit = appPreferences.logsDisplayLimit,
                ) ?: logsDataSource.logs,
                filtersRepository.getAllEnabledAsFlow(),
                queryDataSource.query,
                if (viewingFile.not()) {
                    state
                        .map { it.paused }
                        .distinctUntilChanged()
                } else {
                    flowOf(false)
                },
            ) { logs, filters, query, paused ->
                LogsData(
                    logs = logs,
                    filters = filters,
                    query = query,
                    paused = paused,
                )
            }.scan(LogsData()) { accumulator, data ->
                when {
                    !data.paused
                            // In case they were cleared
                            || data.logs.isEmpty() -> data

                    data.query != accumulator.query
                            || data.filters != accumulator.filters
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
                data.copy(
                    logs = data.logs.filterAndSearch(
                        filters = data.filters,
                        query = data.query,
                    ),
                )
            }.flowOn(
                defaultDispatcher,
            ).onEach { data ->
                reduce {
                    copy(
                        logs = data.logs,
                        query = data.query,
                        filters = data.filters,
                        logsChanged = true,
                    )
                }
            }.launchIn(this)
        }
    }

    fun selectLine(logLine: LogLine, selected: Boolean) = reduce {
        copy(
            selectedItems = selectedItems.toMutableSet().apply {
                if (selected) add(
                    logLine
                ) else remove(
                    logLine
                )
            },
            logsChanged = false,
        )
    }

    fun selectAll() = reduce {
        copy(
            selectedItems = if (selectedItems.containsAll(logs)) {
                emptySet()
            } else {
                logs.toSet()
            },
            logsChanged = false,
        )
    }

    fun selectedToRecording() = launchCatching {
        recordingsRepository.createRecordingFrom(
            lines = withContext(defaultDispatcher) {
                currentState.selectedItems.sortedBy { it.dateAndTime }
            },
        )
    }

    fun exportSelectedLogsTo(uri: Uri) = launchCatching(ioDispatcher) {
        ctx.contentResolver.openOutputStream(uri)?.use {
            it.write(selectedItemsContent.encodeToByteArray())
        }
    }

    fun switchState() = reduce { copy(paused = paused.not(), logsChanged = false) }
    fun pause() = reduce { copy(paused = true, logsChanged = false) }
    fun resume() = reduce { copy(paused = false, logsChanged = false) }

    fun originalOf(logLine: LogLine): String = appPreferences.originalOf(
        logLine = logLine,
        formatDate = ::formatDate,
        formatTime = ::formatTime,
    )

    fun clearSelection() = reduce {
        copy(
            selectedItems = emptySet(),
            logsChanged = false,
        )
    }

    private data class LogsData(
        val logs: List<LogLine> = emptyList(),
        val filters: List<UserFilter> = emptyList(),
        val query: String? = null,
        val paused: Boolean = false,
        val passing: Boolean = true,
    )
}
