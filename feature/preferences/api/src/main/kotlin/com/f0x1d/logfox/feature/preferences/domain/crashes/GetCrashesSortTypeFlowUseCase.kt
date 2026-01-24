package com.f0x1d.logfox.feature.preferences.domain.crashes

import com.f0x1d.logfox.feature.preferences.CrashesSort
import kotlinx.coroutines.flow.Flow

interface GetCrashesSortTypeFlowUseCase {
    operator fun invoke(): Flow<CrashesSort>
}
