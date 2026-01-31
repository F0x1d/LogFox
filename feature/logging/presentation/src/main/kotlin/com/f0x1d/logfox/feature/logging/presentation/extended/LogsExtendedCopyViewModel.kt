package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogsExtendedCopyViewModel @Inject constructor(
    reducer: LogsExtendedCopyReducer,
    effectHandler: LogsExtendedCopyEffectHandler,
    viewStateMapper: LogsExtendedCopyViewStateMapper,
) : BaseStoreViewModel<LogsExtendedCopyViewState, LogsExtendedCopyState, LogsExtendedCopyCommand, LogsExtendedCopySideEffect>(
    initialState = LogsExtendedCopyState(
        text = null,
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(LogsExtendedCopySideEffect.LoadSelectedLines),
)
