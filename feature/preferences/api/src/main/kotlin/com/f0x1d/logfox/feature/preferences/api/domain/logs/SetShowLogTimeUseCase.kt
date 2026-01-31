package com.f0x1d.logfox.feature.preferences.api.domain.logs

interface SetShowLogTimeUseCase {
    operator fun invoke(show: Boolean)
}
