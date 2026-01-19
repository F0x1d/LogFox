package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetPausedFlowUseCase
import com.f0x1d.logfox.feature.logging.impl.data.PausedDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetPausedFlowUseCaseImpl @Inject constructor(
    private val pausedDataSource: PausedDataSource,
) : GetPausedFlowUseCase {
    override fun invoke(): Flow<Boolean> = pausedDataSource.paused
}
