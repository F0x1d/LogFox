package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetLogsUpdateIntervalUseCase {
    operator fun invoke(interval: Long)
}
