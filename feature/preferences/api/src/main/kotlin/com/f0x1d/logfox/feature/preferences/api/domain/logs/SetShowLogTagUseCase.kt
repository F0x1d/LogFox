package com.f0x1d.logfox.feature.preferences.api.domain.logs

interface SetShowLogTagUseCase {
    operator fun invoke(show: Boolean)
}
