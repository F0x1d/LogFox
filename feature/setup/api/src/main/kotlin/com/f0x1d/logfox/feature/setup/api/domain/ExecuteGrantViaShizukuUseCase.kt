package com.f0x1d.logfox.feature.setup.api.domain

interface ExecuteGrantViaShizukuUseCase {
    suspend operator fun invoke(): Boolean
}
