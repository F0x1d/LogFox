package com.f0x1d.logfox.feature.logging.presentation.list

import android.content.Context
import android.net.Uri
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.presentation.di.FileUri
import com.f0x1d.logfox.feature.preferences.domain.GetLogsExpandedUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetResumeLoggingWithBottomTouchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
internal class LogsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    @FileUri fileUri: Uri?,
    reducer: LogsReducer,
    effectHandler: LogsEffectHandler,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    getResumeLoggingWithBottomTouchUseCase: GetResumeLoggingWithBottomTouchUseCase,
    getLogsTextSizeUseCase: GetLogsTextSizeUseCase,
    getLogsExpandedUseCase: GetLogsExpandedUseCase,
    dateTimeFormatter: DateTimeFormatter,
) : BaseStoreViewModel<LogsState, LogsCommand, LogsSideEffect>(
    initialState = LogsState(
        viewingFile = fileUri != null,
        viewingFileName = fileUri?.readFileName(context),
        resumeLoggingWithBottomTouch = getResumeLoggingWithBottomTouchUseCase(),
        logsTextSize = getLogsTextSizeUseCase().toFloat(),
        logsExpanded = getLogsExpandedUseCase(),
        logsFormat = formatLogLineUseCase.showLogValues(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(
        LogsSideEffect.LoadLogs,
        LogsSideEffect.ObservePreferences,
    ),
),
    DateTimeFormatter by dateTimeFormatter {

    fun getSelectedItemsContent(): String = state.value
        .selectedItems
        .sortedBy { it.dateAndTime }
        .joinToString("\n") { originalOf(it) }

    fun originalOf(logLine: LogLine): String = formatLogLineUseCase(
        logLine = logLine,
        formatDate = ::formatDate,
        formatTime = ::formatTime,
    )
}
