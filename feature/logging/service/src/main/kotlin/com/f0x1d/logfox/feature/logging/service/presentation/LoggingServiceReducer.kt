package com.f0x1d.logfox.feature.logging.service.presentation

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class LoggingServiceReducer @Inject constructor() :
    Reducer<LoggingServiceState, LoggingServiceCommand, LoggingServiceSideEffect> {

    override fun reduce(
        state: LoggingServiceState,
        command: LoggingServiceCommand,
    ): ReduceResult<LoggingServiceState, LoggingServiceSideEffect> = when (command) {
        is LoggingServiceCommand.StartLogging -> {
            if (state.isLoggingActive) {
                state.noSideEffects()
            } else {
                state.withSideEffects(LoggingServiceSideEffect.SelectTerminal)
            }
        }

        is LoggingServiceCommand.TerminalSelected -> {
            val terminal = command.terminal
            state.copy(
                currentTerminal = terminal,
                isLoggingActive = true,
            ).withSideEffects(
                LoggingServiceSideEffect.StartLogCollection(terminal),
                LoggingServiceSideEffect.ScheduleLogUpdates,
            )
        }

        is LoggingServiceCommand.StopLogging -> {
            val terminal = state.currentTerminal
            if (terminal != null) {
                state.copy(isLoggingActive = false).withSideEffects(
                    LoggingServiceSideEffect.StopLogCollection,
                    LoggingServiceSideEffect.CancelLogUpdates,
                    LoggingServiceSideEffect.NotifyLoggingStopped,
                    LoggingServiceSideEffect.ExitTerminal(terminal),
                )
            } else {
                state.copy(isLoggingActive = false).noSideEffects()
            }
        }

        is LoggingServiceCommand.RestartLogging -> {
            val terminal = state.currentTerminal
            if (terminal != null) {
                state.copy(isLoggingActive = false).withSideEffects(
                    LoggingServiceSideEffect.StopLogCollection,
                    LoggingServiceSideEffect.CancelLogUpdates,
                    LoggingServiceSideEffect.ExitTerminal(terminal),
                    LoggingServiceSideEffect.SelectTerminal,
                )
            } else {
                state.withSideEffects(LoggingServiceSideEffect.SelectTerminal)
            }
        }

        is LoggingServiceCommand.ClearLogs -> {
            state.withSideEffects(LoggingServiceSideEffect.ClearLogs)
        }

        is LoggingServiceCommand.KillService -> {
            val terminal = state.currentTerminal
            val sideEffects = buildList {
                add(LoggingServiceSideEffect.StopLogCollection)
                add(LoggingServiceSideEffect.CancelLogUpdates)
                add(LoggingServiceSideEffect.NotifyLoggingStopped)
                if (terminal != null) {
                    add(LoggingServiceSideEffect.ExitTerminal(terminal))
                }
                add(LoggingServiceSideEffect.PerformKillService)
            }
            state.copy(isLoggingActive = false).withSideEffects(*sideEffects.toTypedArray())
        }

        is LoggingServiceCommand.TerminalFallback -> {
            val oldTerminal = state.currentTerminal
            val newTerminal = command.newTerminal
            val sideEffects = buildList {
                if (oldTerminal != null) {
                    add(LoggingServiceSideEffect.ExitTerminal(oldTerminal))
                }
                add(LoggingServiceSideEffect.StartLogCollection(newTerminal))
            }
            state.copy(currentTerminal = newTerminal).withSideEffects(*sideEffects.toTypedArray())
        }

        is LoggingServiceCommand.LoggingError -> {
            state.withSideEffects(
                LoggingServiceSideEffect.HandleLoggingError(
                    error = command.error,
                    terminal = command.terminal,
                ),
            )
        }

        is LoggingServiceCommand.LoggingFlowCompleted -> {
            // Logging flow completed normally (e.g., terminal process ended)
            // Restart the logging flow if still active
            val terminal = state.currentTerminal
            if (state.isLoggingActive && terminal != null) {
                state.withSideEffects(
                    LoggingServiceSideEffect.StartLogCollection(terminal),
                )
            } else {
                state.noSideEffects()
            }
        }

        is LoggingServiceCommand.ShowToast -> {
            state.withSideEffects(LoggingServiceSideEffect.ShowToast(command.message))
        }
    }
}
