package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.flow.Flow

internal interface SelectedLogLinesDataSource {
    val selectedLines: Flow<List<LogLine>>

    suspend fun updateSelectedLines(selectedLines: List<LogLine>)
}
