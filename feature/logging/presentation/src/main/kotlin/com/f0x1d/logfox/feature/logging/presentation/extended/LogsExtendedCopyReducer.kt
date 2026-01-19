package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class LogsExtendedCopyReducer @Inject constructor() : Reducer<LogsExtendedCopyState, LogsExtendedCopyCommand, LogsExtendedCopySideEffect> {

    override fun reduce(
        state: LogsExtendedCopyState,
        command: LogsExtendedCopyCommand,
    ): ReduceResult<LogsExtendedCopyState, LogsExtendedCopySideEffect> = when (command) {
        is LogsExtendedCopyCommand.Load -> {
            state.withSideEffects(LogsExtendedCopySideEffect.LoadSelectedLines)
        }

        is LogsExtendedCopyCommand.TextLoaded -> {
            state.copy(text = command.text).noSideEffects()
        }
    }
}
