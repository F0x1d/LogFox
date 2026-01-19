package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

interface FormatLogLineUseCase {
    operator fun invoke(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String

    fun showLogValues(): ShowLogValues
}
