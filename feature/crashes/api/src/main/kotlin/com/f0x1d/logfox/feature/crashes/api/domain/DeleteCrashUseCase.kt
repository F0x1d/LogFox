package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

interface DeleteCrashUseCase {
    suspend operator fun invoke(appCrash: AppCrash)
}
