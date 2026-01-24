package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface UpdateSelectedLogLinesUseCase {
    suspend operator fun invoke(selectedLines: List<LogLine>)
}
