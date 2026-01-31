package com.f0x1d.logfox.feature.preferences.api.domain.datetime

interface SetDateFormatUseCase {
    operator fun invoke(format: String)
}
