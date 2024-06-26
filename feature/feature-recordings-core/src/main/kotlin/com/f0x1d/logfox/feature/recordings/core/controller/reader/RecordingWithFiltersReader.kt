package com.f0x1d.logfox.feature.recordings.core.controller.reader

import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.logging.core.model.filterAndSearch
import com.f0x1d.logfox.feature.recordings.core.controller.reader.base.RecordingReader
import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

open class RecordingWithFiltersReader @Inject constructor(): RecordingReader() {

    private var filters = emptyList<UserFilter>()
    private val filtersMutex = Mutex()

    override suspend fun shouldRecordLine(line: LogLine) = filtersMutex.withLock {
        listOf(line)
            .filterAndSearch(filters)
            .isNotEmpty()
    }

    suspend fun updateFilters(newFilters: List<UserFilter>) = filtersMutex.withLock {
        this.filters = newFilters
    }
}
