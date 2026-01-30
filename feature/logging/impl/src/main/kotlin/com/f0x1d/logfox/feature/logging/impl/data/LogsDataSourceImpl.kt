package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LogsDataSourceImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : LogsDataSource {

    private val mutableLogs = MutableStateFlow(emptyList<LogLine>())
    private val logsById = ConcurrentHashMap<Long, LogLine>()

    override val logs: Flow<List<LogLine>> get() = mutableLogs.asStateFlow()

    override fun getByIds(ids: Set<Long>): List<LogLine> =
        ids.mapNotNull { logsById[it] }

    override suspend fun updateLogs(logs: List<LogLine>) = withContext(defaultDispatcher) {
        logsById.clear()
        logs.forEach { logsById[it.id] = it }
        mutableLogs.update { logs.toMutableList() }
    }
}
