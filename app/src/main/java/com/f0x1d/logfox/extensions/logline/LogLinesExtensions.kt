package com.f0x1d.logfox.extensions.logline

import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.LogLine

fun List<LogLine>.filterAndSearch(filters: List<UserFilter>, query: String?) = filterByExtendedFilters(
    filters.filter { it.enabled }
).let {
    if (query == null)
        it
    else
        it.filter { logLine -> logLine.tag.contains(query) || logLine.content.contains(query) }
}

fun List<LogLine>.filterByExtendedFilters(filters: List<UserFilter>): List<LogLine> {
    if (filters.isEmpty()) return this

    val includingFilters = filters.filter { it.including }
    val excludingFilters = filters.filter { !it.including }

    return filter { logLine ->
        val shouldExclude = excludingFilters.any {
            it.allowedLevels.contains(logLine.level) &&
                    it.pid.equalsOrTrueIfNull(logLine.pid) &&
                    it.tid.equalsOrTrueIfNull(logLine.tid) &&
                    it.tag.equalsOrTrueIfNull(logLine.tag) &&
                    it.content.containsOrTrueIfNull(logLine.content)
        }

        if (shouldExclude)
            false
        else includingFilters.run {
            if (isEmpty())
                true
            else any {
                it.allowedLevels.contains(logLine.level) &&
                        it.pid.equalsOrTrueIfNull(logLine.pid) &&
                        it.tid.equalsOrTrueIfNull(logLine.tid) &&
                        it.tag.equalsOrTrueIfNull(logLine.tag) &&
                        it.content.containsOrTrueIfNull(logLine.content)
            }
        }
    }
}

private fun String?.equalsOrTrueIfNull(other: String) = if (this == null) true else other == this
private fun String?.containsOrTrueIfNull(other: String) = if (this == null) true else other.contains(this)