package com.f0x1d.logfox.feature.filters.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLevel

interface CreateFilterUseCase {
    suspend operator fun invoke(
        including: Boolean,
        enabled: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    )
}
