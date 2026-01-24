package com.f0x1d.logfox.feature.logging.api.domain

interface UpdatePausedUseCase {
    suspend operator fun invoke(paused: Boolean)
}
