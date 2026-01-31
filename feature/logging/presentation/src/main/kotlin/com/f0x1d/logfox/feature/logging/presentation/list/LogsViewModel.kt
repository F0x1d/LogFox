package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsExpandedUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogsViewModel @Inject constructor(
    reducer: LogsReducer,
    effectHandler: LogsEffectHandler,
    getResumeLoggingWithBottomTouchUseCase: GetResumeLoggingWithBottomTouchUseCase,
    getLogsExpandedUseCase: GetLogsExpandedUseCase,
    getLogsTextSizeUseCase: GetLogsTextSizeUseCase,
    getShowLogValuesUseCase: GetShowLogValuesUseCase,
    getQueryUseCase: GetQueryUseCase,
    getCaseSensitiveUseCase: GetCaseSensitiveUseCase,
) : BaseStoreViewModel<LogsState, LogsCommand, LogsSideEffect>(
    initialState = LogsState(
        logs = null,
        paused = false,
        query = getQueryUseCase(),
        caseSensitive = getCaseSensitiveUseCase(),
        filters = emptyList(),
        showLogValues = getShowLogValuesUseCase(),
        selectedIds = emptySet(),
        expandedOverrides = emptyMap(),
        logsExpanded = getLogsExpandedUseCase(),
        textSize = getLogsTextSizeUseCase(),
        logsChanged = true,
        resumeLoggingWithBottomTouch = getResumeLoggingWithBottomTouchUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(
        LogsSideEffect.LoadLogs,
        LogsSideEffect.ObservePreferences,
    ),
)
