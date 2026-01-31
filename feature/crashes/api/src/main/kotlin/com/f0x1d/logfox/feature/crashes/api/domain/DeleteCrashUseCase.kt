package com.f0x1d.logfox.feature.crashes.api.domain

interface DeleteCrashUseCase {
    suspend operator fun invoke(crashId: Long)
}
