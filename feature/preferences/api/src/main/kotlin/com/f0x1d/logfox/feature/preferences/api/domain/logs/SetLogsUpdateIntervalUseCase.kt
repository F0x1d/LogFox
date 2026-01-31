package com.f0x1d.logfox.feature.preferences.api.domain.logs

interface SetLogsUpdateIntervalUseCase {
    operator fun invoke(interval: Long)
}
