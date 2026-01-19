package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.database.model.AppCrash

interface DeleteAllCrashesByPackageNameUseCase {
    suspend operator fun invoke(appCrash: AppCrash)
}
