package com.f0x1d.logfox.feature.crashes.api.domain

interface UpdateCrashesSearchQueryUseCase {
    operator fun invoke(query: String)
}
