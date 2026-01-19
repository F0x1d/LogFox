package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetLogsDisplayLimitUseCase {
    operator fun invoke(limit: Int)
}
