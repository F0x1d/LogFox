package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsExpandedUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetResumeLoggingWithBottomTouchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
internal class LogsViewModel @Inject constructor(
    reducer: LogsReducer,
    effectHandler: LogsEffectHandler,
    viewStateMapper: LogsViewStateMapper,
    getResumeLoggingWithBottomTouchUseCase: GetResumeLoggingWithBottomTouchUseCase,
    getLogsExpandedUseCase: GetLogsExpandedUseCase,
    getLogsTextSizeUseCase: GetLogsTextSizeUseCase,
    getShowLogValuesUseCase: GetShowLogValuesUseCase,
    getQueryUseCase: GetQueryUseCase,
    getCaseSensitiveUseCase: GetCaseSensitiveUseCase,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
) : BaseStoreViewModel<LogsViewState, LogsState, LogsCommand, LogsSideEffect>(
    initialState = LogsState(
        logs = emptyList(),
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
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(
        LogsSideEffect.LoadLogs,
        LogsSideEffect.ObservePreferences,
    ),
    viewStateMappingDispatcher = defaultDispatcher,
)
