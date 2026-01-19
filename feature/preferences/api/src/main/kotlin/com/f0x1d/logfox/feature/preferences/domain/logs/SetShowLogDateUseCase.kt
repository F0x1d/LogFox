package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetShowLogDateUseCase {
    operator fun invoke(show: Boolean)
}
