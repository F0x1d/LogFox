package com.f0x1d.logfox.feature.logging.core.model

import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.logline.LogLine

fun List<LogLine>.filterAndSearch(filters: List<UserFilter>, query: String? = null) = filterByExtendedFilters(
    filters.filter { it.enabled }
).let {
    if (query == null)
        it
    else
        it.filter { logLine -> logLine.tag.contains(query) || logLine.content.contains(query) }
}

private fun List<LogLine>.filterByExtendedFilters(filters: List<UserFilter>): List<LogLine> {
    if (filters.isEmpty()) return this

    val includingFilters = filters.filter { it.including }
    val excludingFilters = filters.filter { !it.including }

    return filter { logLine ->
        val shouldExclude = excludingFilters.any {
            it.lineSuits(logLine)
        }

        if (shouldExclude)
            false
        else includingFilters.run {
            if (isEmpty())
                true
            else any {
                it.lineSuits(logLine)
            }
        }
    }
}

private fun UserFilter.lineSuits(logLine: LogLine) = allowedLevels.contains(logLine.level) &&
        uid.equalsOrTrueIfNull(logLine.uid) &&
        pid.equalsOrTrueIfNull(logLine.pid) &&
        tid.equalsOrTrueIfNull(logLine.tid) &&
        packageName.equalsOrTrueIfNull(logLine.packageName ?: "") &&
        tag.equalsOrTrueIfNull(logLine.tag) &&
        content.containsOrTrueIfNull(logLine.content)

private fun String?.equalsOrTrueIfNull(other: String) = if (this == null) true else other == this
private fun String?.containsOrTrueIfNull(other: String) = if (this == null) true else other.contains(this)
