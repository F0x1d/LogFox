package com.f0x1d.logfox.feature.logging.impl.data

import kotlinx.coroutines.flow.Flow

internal interface PausedDataSource {
    val paused: Flow<Boolean>

    suspend fun updatePaused(paused: Boolean)
}
