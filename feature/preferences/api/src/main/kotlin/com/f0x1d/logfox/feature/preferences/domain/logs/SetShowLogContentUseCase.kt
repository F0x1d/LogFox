package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetShowLogContentUseCase {
    operator fun invoke(show: Boolean)
}
