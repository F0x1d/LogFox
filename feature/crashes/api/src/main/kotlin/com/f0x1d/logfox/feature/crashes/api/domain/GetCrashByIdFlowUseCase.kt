package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.database.model.AppCrash
import kotlinx.coroutines.flow.Flow

interface GetCrashByIdFlowUseCase {
    operator fun invoke(id: Long): Flow<AppCrash?>
}
