package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.extensions.logline.filterAndSearch
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
    private val database: AppDatabase,
    private val loggingRepository: LoggingRepository,
    val appPreferences: AppPreferences,
    application: Application
): BaseViewModel(application) {

    val query = MutableStateFlow<String?>(null)

    val paused = MutableStateFlow(false)
    val pausedData = paused.asLiveData()

    val logs = combine(
        loggingRepository.logsFlow,
        database.userFilterDao().getAllAsFlow(),
        query,
        paused
    ) { logs, filters, query, paused ->
        LogsData(logs, filters, query, paused)
    }.scan(null as LogsData?) { accumulator, data ->
        when {
            !data.paused -> data

            data.query != accumulator?.query -> data.copy(logs = accumulator?.logs ?: data.logs)
            data.filters != accumulator?.filters -> data.copy(logs = accumulator?.logs ?: data.logs)

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
    val filters: List<UserFilter>,
    val query: String?,
    val paused: Boolean,
    val passing: Boolean = true
)