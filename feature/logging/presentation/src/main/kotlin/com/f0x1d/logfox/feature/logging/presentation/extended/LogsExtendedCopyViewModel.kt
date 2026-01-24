package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogsExtendedCopyViewModel @Inject constructor(
    reducer: LogsExtendedCopyReducer,
    effectHandler: LogsExtendedCopyEffectHandler,
) : BaseStoreViewModel<LogsExtendedCopyState, LogsExtendedCopyCommand, LogsExtendedCopySideEffect>(
    initialState = LogsExtendedCopyState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(LogsExtendedCopySideEffect.LoadSelectedLines),
)
