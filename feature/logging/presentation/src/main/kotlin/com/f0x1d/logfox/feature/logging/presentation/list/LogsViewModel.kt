package com.f0x1d.logfox.feature.logging.presentation.list

import android.net.Uri
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.domain.GetFileNameUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesUseCase
import com.f0x1d.logfox.feature.logging.presentation.di.FileUri
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogsViewModel @Inject constructor(
    @FileUri fileUri: Uri?,
    reducer: LogsReducer,
    effectHandler: LogsEffectHandler,
    getResumeLoggingWithBottomTouchUseCase: GetResumeLoggingWithBottomTouchUseCase,
    getLogsTextSizeUseCase: GetLogsTextSizeUseCase,
    getLogsExpandedUseCase: GetLogsExpandedUseCase,
    getShowLogValuesUseCase: GetShowLogValuesUseCase,
    getFileNameUseCase: GetFileNameUseCase,
    dateTimeFormatter: DateTimeFormatter,
) : BaseStoreViewModel<LogsState, LogsCommand, LogsSideEffect>(
    initialState = LogsState(
        viewingFile = fileUri != null,
        viewingFileName = fileUri?.let(getFileNameUseCase::invoke),
        resumeLoggingWithBottomTouch = getResumeLoggingWithBottomTouchUseCase(),
        logsTextSize = getLogsTextSizeUseCase().toFloat(),
        logsExpanded = getLogsExpandedUseCase(),
        logsFormat = getShowLogValuesUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(
        LogsSideEffect.LoadLogs,
        LogsSideEffect.ObservePreferences,
    ),
),
    DateTimeFormatter by dateTimeFormatter
