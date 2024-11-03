package com.f0x1d.logfox.feature.logging.api.presentation

interface LoggingServiceDelegate {
    fun clearLogs()
    fun restartLogging()
    fun killService()
}
