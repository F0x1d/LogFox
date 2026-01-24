package com.f0x1d.logfox.feature.logging.api.domain

interface GetLastLogIdUseCase {
    suspend operator fun invoke(): Long
}
