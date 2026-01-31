package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogsExtendedCopyViewModel @Inject constructor(
    reducer: LogsExtendedCopyReducer,
    effectHandler: LogsExtendedCopyEffectHandler,
) : BaseStoreViewModel<LogsExtendedCopyState, LogsExtendedCopyState, LogsExtendedCopyCommand, LogsExtendedCopySideEffect>(
    initialState = LogsExtendedCopyState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
    initialSideEffects = listOf(LogsExtendedCopySideEffect.LoadSelectedLines),
)
