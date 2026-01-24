package com.f0x1d.logfox.feature.setup.api.domain

interface ExecuteGrantViaRootUseCase {
    suspend operator fun invoke(): Boolean
}
