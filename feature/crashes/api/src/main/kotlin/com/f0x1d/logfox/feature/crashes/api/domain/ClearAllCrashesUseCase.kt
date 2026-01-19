package com.f0x1d.logfox.feature.crashes.api.domain

interface ClearAllCrashesUseCase {
    suspend operator fun invoke()
}
