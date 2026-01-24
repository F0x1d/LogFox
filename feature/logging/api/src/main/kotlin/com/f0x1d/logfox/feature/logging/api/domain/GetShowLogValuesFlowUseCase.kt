package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import kotlinx.coroutines.flow.Flow

interface GetShowLogValuesFlowUseCase {
    operator fun invoke(): Flow<ShowLogValues>
}
