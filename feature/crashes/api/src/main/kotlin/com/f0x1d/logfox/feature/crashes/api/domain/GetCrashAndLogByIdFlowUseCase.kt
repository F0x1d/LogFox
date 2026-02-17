package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import kotlinx.coroutines.flow.Flow

interface GetCrashAndLogByIdFlowUseCase {
    suspend operator fun invoke(id: Long): Flow<Pair<AppCrash, String?>?>
}
