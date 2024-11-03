package com.f0x1d.logfox.feature.logging.api.data

import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.flow.Flow

interface SelectedLogLinesDataSource {
    val selectedLines: Flow<List<LogLine>>

    suspend fun updateSelectedLines(selectedLines: List<LogLine>)
}
