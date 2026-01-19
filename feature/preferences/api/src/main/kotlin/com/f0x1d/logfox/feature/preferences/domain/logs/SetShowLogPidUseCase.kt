package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetShowLogPidUseCase {
    operator fun invoke(show: Boolean)
}
