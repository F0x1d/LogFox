package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.flow.Flow

internal interface LogsDataSource {
    val logs: Flow<List<LogLine>>

    suspend fun updateLogs(logs: List<LogLine>)
}
