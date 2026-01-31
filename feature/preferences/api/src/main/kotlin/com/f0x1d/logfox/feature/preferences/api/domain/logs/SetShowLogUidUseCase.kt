package com.f0x1d.logfox.feature.preferences.api.domain.logs

interface SetShowLogUidUseCase {
    operator fun invoke(show: Boolean)
}
