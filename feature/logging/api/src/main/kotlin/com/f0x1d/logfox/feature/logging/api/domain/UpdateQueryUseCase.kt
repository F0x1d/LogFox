package com.f0x1d.logfox.feature.logging.api.domain

interface UpdateQueryUseCase {
    suspend operator fun invoke(query: String?)
}
