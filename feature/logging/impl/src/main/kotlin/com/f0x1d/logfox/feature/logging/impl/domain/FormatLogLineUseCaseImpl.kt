package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import javax.inject.Inject

internal class FormatLogLineUseCaseImpl @Inject constructor(
    private val logLineFormatterRepository: LogLineFormatterRepository,
) : FormatLogLineUseCase {

    override fun invoke(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String = logLineFormatterRepository.format(
        logLine = logLine,
        formatDate = formatDate,
        formatTime = formatTime,
    )
}
