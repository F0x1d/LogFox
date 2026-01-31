package com.f0x1d.logfox.feature.preferences.api.domain.ui

import kotlinx.coroutines.flow.Flow

interface GetNightThemeFlowUseCase {
    operator fun invoke(): Flow<Int>
}
