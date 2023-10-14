package com.f0x1d.logfox.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.di.viewmodel.FileUri
import com.f0x1d.logfox.extensions.logline.filterAndSearch
import com.f0x1d.logfox.extensions.readFileContentsAsFlow
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    @FileUri val fileUri: Uri?,
    private val database: AppDatabase,
    private val loggingRepository: LoggingRepository,
    val appPreferences: AppPreferences,
    application: Application
): BaseViewModel(application) {

    val query = MutableStateFlow<String?>(null)

    val paused = MutableStateFlow(false)

    val viewingFile = MutableStateFlow(fileUri != null)

    val selectedItems = MutableStateFlow(emptyList<LogLine>())

    @Suppress("UNCHECKED_CAST")
    val logs = combine(
        loggingRepository.logsFlow,
        fileUri.readFileContentsAsFlow(ctx, appPreferences),
        viewingFile,
        database.userFilterDao().getAllAsFlow(),
        query,
        paused
    ) { values ->
        val logs = values[0] as List<LogLine>
        val fileLogs = values[1] as List<LogLine>
        val viewingFile = values[2] as Boolean
        val filters = values[3] as List<UserFilter>
        val query = values[4] as String?
        val paused = values[5] as Boolean

        val resultLogs = when {
            viewingFile -> fileLogs
            else -> logs
        }

        LogsData(resultLogs, viewingFile, filters, query, paused)
    }.scan(null as LogsData?) { accumulator, data ->
        when {
            !data.paused || data.viewingFile != accumulator?.viewingFile -> data

            data.query != accumulator.query -> data.copy(logs = accumulator.logs)
            data.filters != accumulator.filters -> data.copy(logs = accumulator.logs)

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
    ).asLiveData()

    val serviceRunningData = loggingRepository.serviceRunningFlow.asLiveData()

    val resumeLoggingWithBottomTouch get() = appPreferences.resumeLoggingWithBottomTouch

    fun stopViewingFile() = viewingFile.apply {
        selectedItems.update { emptyList() }

        if (value) update {
            false
        }
    }

    fun selectLine(logLine: LogLine, selected: Boolean) = selectedItems.updateList {
        if (selected) add(
            logLine
        ) else remove(
            logLine
        )
    }

    fun query(query: String?) = this.query.update { query }

    fun clearLogs() = loggingRepository.clearLogs()

    fun restartLogging() = loggingRepository.restartLogging()

    fun switchState() = if (!paused.value)
        pause()
    else
        resume()

    fun pause() = paused.update { true }
    fun resume() = paused.update { false }
}

data class LogsData(
    val logs: List<LogLine>,
    val viewingFile: Boolean,
    val filters: List<UserFilter>,
    val query: String?,
    val paused: Boolean,
    val passing: Boolean = true
)