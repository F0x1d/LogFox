package com.f0x1d.logfox.feature.preferences.domain.datetime

interface SetTimeFormatUseCase {
    operator fun invoke(format: String)
}
