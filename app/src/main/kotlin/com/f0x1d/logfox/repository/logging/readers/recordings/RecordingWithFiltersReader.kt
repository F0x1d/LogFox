package com.f0x1d.logfox.repository.logging.readers.recordings

import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.extensions.logline.filterAndSearch
import com.f0x1d.logfox.repository.logging.readers.recordings.base.RecordingReader
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

open class RecordingWithFiltersReader @Inject constructor(): RecordingReader() {

    private var filters = emptyList<UserFilter>()
    private val filtersMutex = Mutex()

    override suspend fun shouldRecordLine(line: com.f0x1d.logfox.model.LogLine) = filtersMutex.withLock {
        listOf(line)
            .filterAndSearch(filters)
            .isNotEmpty()
    }

    suspend fun updateFilters(newFilters: List<UserFilter>) = filtersMutex.withLock {
        this.filters = newFilters
    }
}
