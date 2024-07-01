package com.f0x1d.logfox.feature.logging.core.store

import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

// I completely don't like it
// I really want to get rid of Singletons in project
interface LoggingStore {
    val logs: Flow<List<LogLine>>

    fun updateLogs(logs: List<LogLine>)
}

@Singleton
internal class LoggingStoreImpl @Inject constructor() : LoggingStore {

    private val mutableLogs = MutableStateFlow(emptyList<LogLine>())

    override val logs: Flow<List<LogLine>> = mutableLogs

    override fun updateLogs(logs: List<LogLine>) {
        mutableLogs.update { logs.toMutableList() }
    }
}
