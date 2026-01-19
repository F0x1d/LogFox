package com.f0x1d.logfox.feature.crashes.api.domain

interface CheckAppDisabledUseCase {
    suspend operator fun invoke(packageName: String)
    suspend operator fun invoke(packageName: String, checked: Boolean)
}
