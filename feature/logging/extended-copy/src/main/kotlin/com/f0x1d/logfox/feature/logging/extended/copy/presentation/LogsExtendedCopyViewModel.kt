package com.f0x1d.logfox.feature.logging.extended.copy.presentation

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.data.SelectedLogLinesDataSource
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsExtendedCopyViewModel @Inject constructor(
    private val selectedLogLinesDataSource: SelectedLogLinesDataSource,
    private val appPreferences: AppPreferences,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    dateTimeFormatter: DateTimeFormatter,
    application: Application,
) : BaseViewModel<LogsExtendedCopyState, LogsExtendedCopyAction>(
    initialStateProvider = { LogsExtendedCopyState() },
    application = application,
), DateTimeFormatter by dateTimeFormatter {
    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            selectedLogLinesDataSource.selectedLines
                .map { lines ->
                    lines.joinToString("\n") { line ->
                        appPreferences.originalOf(
                            logLine = line,
                            formatDate = ::formatDate,
                            formatTime = ::formatTime,
                        )
                    }
                }
                .flowOn(defaultDispatcher)
                .collect { text ->
                    reduce { copy(text = text) }
                }
        }
    }
}
