package com.f0x1d.logfox.feature.preferences.domain.crashes

interface GetOpenCrashesOnStartupUseCase {
    operator fun invoke(): Boolean
}
