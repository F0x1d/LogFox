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
    @ApplicationContext private val context: Context,
    @FileUri val fileUri: Uri?,
    reducer: LogsReducer,
    effectHandler: LogsEffectHandler,
    private val getResumeLoggingWithBottomTouchUseCase: GetResumeLoggingWithBottomTouchUseCase,
    private val getLogsTextSizeUseCase: GetLogsTextSizeUseCase,
    private val getLogsExpandedUseCase: GetLogsExpandedUseCase,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    dateTimeFormatter: DateTimeFormatter,
) : BaseStoreViewModel<LogsState, LogsCommand, LogsSideEffect>(
    initialState = LogsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffect = LogsSideEffect.LoadLogs,
),
    DateTimeFormatter by dateTimeFormatter {

    val viewingFile = fileUri != null
    val viewingFileName = fileUri?.readFileName(context)

    val resumeLoggingWithBottomTouch get() = getResumeLoggingWithBottomTouchUseCase()
    val logsTextSize get() = getLogsTextSizeUseCase().toFloat()
    val logsExpanded get() = getLogsExpandedUseCase()
    val logsFormat get() = formatLogLineUseCase.showLogValues()

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
