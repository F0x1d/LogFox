package com.f0x1d.logfox.feature.logging.presentation.service

import com.f0x1d.logfox.feature.terminals.base.Terminal

internal sealed interface LoggingServiceSideEffect {
    // Business logic (handled by EffectHandler)
    data object SelectTerminal : LoggingServiceSideEffect
    data class StartLogCollection(val terminal: Terminal) : LoggingServiceSideEffect
    data object StopLogCollection : LoggingServiceSideEffect
    data object ClearLogs : LoggingServiceSideEffect
    data object ScheduleLogUpdates : LoggingServiceSideEffect
    data object CancelLogUpdates : LoggingServiceSideEffect
    data object NotifyLoggingStopped : LoggingServiceSideEffect
    data class ExitTerminal(val terminal: Terminal) : LoggingServiceSideEffect
    data class HandleLoggingError(val error: Throwable, val terminal: Terminal) : LoggingServiceSideEffect

    // UI side effects (handled by Service)
    data class ShowToast(val message: String) : LoggingServiceSideEffect
    data object PerformKillService : LoggingServiceSideEffect
}
