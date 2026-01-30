package com.f0x1d.logfox.feature.crashes.api.domain

interface DeleteAllCrashesByPackageNameUseCase {
    suspend operator fun invoke(packageName: String)
}
