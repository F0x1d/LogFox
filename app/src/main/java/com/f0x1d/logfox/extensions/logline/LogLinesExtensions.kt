package com.f0x1d.logfox.extensions.logline

import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.FiltersRepository

fun List<LogLine>.filterAndSearch(filtersRepository: FiltersRepository, query: String?) = filterByExtendedFilters(filtersRepository.itemsFlow.value.filter { it.enabled })
    .let {
        if (query == null)
            it
        else
            it.filter { it.tag.contains(query) || it.content.contains(query) }
    }

fun List<LogLine>.filterByExtendedFilters(filters: List<UserFilter>) = filter { logLine ->
    if (filters.isEmpty())
        true
    else
        filters.any {
            it.allowedLevels.contains(logLine.level) &&
                    it.pid.equalsOrTrueIfNull(logLine.pid) &&
                    it.tid.equalsOrTrueIfNull(logLine.tid) &&
                    it.tag.equalsOrTrueIfNull(logLine.tag) &&
                    it.content.containsOrTrueIfNull(logLine.content)
        }
}

private fun String?.equalsOrTrueIfNull(other: String) = if (this == null) true else other == this
private fun String?.containsOrTrueIfNull(other: String) = if (this == null) true else other.contains(this)