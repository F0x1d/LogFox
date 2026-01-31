package com.f0x1d.logfox.feature.logging.presentation.service

import com.f0x1d.logfox.feature.terminals.base.Terminal

internal data class LoggingServiceState(
    val currentTerminal: Terminal? = null,
    val isLoggingActive: Boolean = false,
)
