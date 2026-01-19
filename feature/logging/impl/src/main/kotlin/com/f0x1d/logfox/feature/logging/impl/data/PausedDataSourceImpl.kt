package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.core.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PausedDataSourceImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : PausedDataSource {
    private val mutablePaused = MutableStateFlow(false)

    override val paused: Flow<Boolean> get() = mutablePaused.asStateFlow()

    override suspend fun updatePaused(paused: Boolean) = withContext(defaultDispatcher) {
        mutablePaused.update { paused }
    }
}
