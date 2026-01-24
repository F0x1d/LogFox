package com.f0x1d.logfox.feature.logging.service.presentation

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.terminals.base.Terminal

internal sealed interface LoggingServiceCommand {
    // User actions
    data object StartLogging : LoggingServiceCommand
    data object StopLogging : LoggingServiceCommand
    data object RestartLogging : LoggingServiceCommand
    data object ClearLogs : LoggingServiceCommand
    data object KillService : LoggingServiceCommand

    // Feedback from effect handler
    data class TerminalSelected(val terminal: Terminal) : LoggingServiceCommand
    data class TerminalFallback(val newTerminal: Terminal) : LoggingServiceCommand
    data class LoggingError(val error: Throwable, val terminal: Terminal) : LoggingServiceCommand
    data object LoggingFlowCompleted : LoggingServiceCommand
    data class ShowToast(val message: String) : LoggingServiceCommand
}
