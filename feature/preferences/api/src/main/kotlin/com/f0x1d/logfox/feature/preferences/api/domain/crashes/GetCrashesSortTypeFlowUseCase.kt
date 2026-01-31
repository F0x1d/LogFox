package com.f0x1d.logfox.feature.preferences.api.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.CrashesSort
import kotlinx.coroutines.flow.Flow

interface GetCrashesSortTypeFlowUseCase {
    operator fun invoke(): Flow<CrashesSort>
}
