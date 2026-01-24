package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetShowLogTimeUseCase {
    operator fun invoke(show: Boolean)
}
