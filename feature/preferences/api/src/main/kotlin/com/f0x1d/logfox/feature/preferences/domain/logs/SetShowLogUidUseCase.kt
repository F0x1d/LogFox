package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetShowLogUidUseCase {
    operator fun invoke(show: Boolean)
}
