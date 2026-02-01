package com.f0x1d.logfox.feature.preferences.api.domain.crashes

interface SetWrapCrashLogLinesUseCase {
    operator fun invoke(wrap: Boolean)
}
