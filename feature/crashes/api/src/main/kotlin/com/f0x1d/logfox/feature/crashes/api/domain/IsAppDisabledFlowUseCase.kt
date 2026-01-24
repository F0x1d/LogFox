package com.f0x1d.logfox.feature.crashes.api.domain

import kotlinx.coroutines.flow.Flow

interface IsAppDisabledFlowUseCase {
    operator fun invoke(packageName: String): Flow<Boolean>
}
