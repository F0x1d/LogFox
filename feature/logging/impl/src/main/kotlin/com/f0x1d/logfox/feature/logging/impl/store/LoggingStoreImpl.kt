package com.f0x1d.logfox.feature.logging.impl.store

import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.feature.logging.api.store.LoggingStore
import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LoggingStoreImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : LoggingStore {

    private val mutableLogs = MutableStateFlow(emptyList<LogLine>())

    override val logs: Flow<List<LogLine>> = mutableLogs

    override suspend fun updateLogs(logs: List<LogLine>) = withContext(defaultDispatcher) {
        mutableLogs.update { logs.toMutableList() }
    }
}
