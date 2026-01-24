package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import kotlinx.coroutines.flow.Flow

interface GetAllCrashesFlowUseCase {
    operator fun invoke(): Flow<List<AppCrash>>
}
