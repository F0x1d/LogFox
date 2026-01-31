package com.f0x1d.logfox.feature.preferences.api.domain.datetime

interface SetTimeFormatUseCase {
    operator fun invoke(format: String)
}
