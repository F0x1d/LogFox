package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.UpdatePausedUseCase
import com.f0x1d.logfox.feature.logging.impl.data.PausedDataSource
import javax.inject.Inject

internal class UpdatePausedUseCaseImpl @Inject constructor(
    private val pausedDataSource: PausedDataSource,
) : UpdatePausedUseCase {
    override suspend fun invoke(paused: Boolean) {
        pausedDataSource.updatePaused(paused)
    }
}
