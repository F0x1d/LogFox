package com.f0x1d.logfox.feature.preferences.api.domain.logs

interface SetShowLogPackageUseCase {
    operator fun invoke(show: Boolean)
}
