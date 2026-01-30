package com.f0x1d.logfox.feature.logging.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.domain.logs.GetResumeLoggingWithBottomTouchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogsViewModel @Inject constructor(
    reducer: LogsReducer,
    effectHandler: LogsEffectHandler,
    getResumeLoggingWithBottomTouchUseCase: GetResumeLoggingWithBottomTouchUseCase,
) : BaseStoreViewModel<LogsState, LogsCommand, LogsSideEffect>(
    initialState = LogsState(
        resumeLoggingWithBottomTouch = getResumeLoggingWithBottomTouchUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(
        LogsSideEffect.LoadLogs,
        LogsSideEffect.ObservePreferences,
        LogsSideEffect.ObserveSelection,
        LogsSideEffect.ObservePausedState,
    ),
)
