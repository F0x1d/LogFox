package com.f0x1d.logfox.feature.logging.api.store

import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.flow.Flow

// I completely don't like it
// I really want to get rid of Singletons in project
interface LoggingStore {
    val logs: Flow<List<LogLine>>

    suspend fun updateLogs(logs: List<LogLine>)
}
