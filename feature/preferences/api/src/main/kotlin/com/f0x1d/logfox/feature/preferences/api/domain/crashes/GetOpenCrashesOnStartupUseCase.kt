package com.f0x1d.logfox.feature.preferences.api.domain.crashes

interface GetOpenCrashesOnStartupUseCase {
    operator fun invoke(): Boolean
}
