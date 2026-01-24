package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

interface GetShowLogValuesUseCase {
    operator fun invoke(): ShowLogValues
}
