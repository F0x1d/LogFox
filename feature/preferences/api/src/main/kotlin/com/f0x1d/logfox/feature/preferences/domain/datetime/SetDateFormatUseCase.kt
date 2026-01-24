package com.f0x1d.logfox.feature.preferences.domain.datetime

interface SetDateFormatUseCase {
    operator fun invoke(format: String)
}
