package com.f0x1d.logfox.feature.logging.api.domain

interface UpdateCaseSensitiveUseCase {
    suspend operator fun invoke(caseSensitive: Boolean)
}
