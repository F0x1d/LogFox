package com.f0x1d.logfox.feature.preferences.domain.logs

interface SetShowLogPackageUseCase {
    operator fun invoke(show: Boolean)
}
