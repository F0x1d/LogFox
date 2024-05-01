package com.f0x1d.logfox.extensions.logline

import com.f0x1d.logfox.database.entity.UserFilter

fun List<com.f0x1d.logfox.model.LogLine>.filterAndSearch(filters: List<UserFilter>, query: String? = null) = filterByExtendedFilters(
    filters.filter { it.enabled }
).let {
    if (query == null)
        it
    else
        it.filter { logLine -> logLine.tag.contains(query) || logLine.content.contains(query) }
}

private fun List<com.f0x1d.logfox.model.LogLine>.filterByExtendedFilters(filters: List<UserFilter>): List<com.f0x1d.logfox.model.LogLine> {
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

private fun UserFilter.lineSuits(logLine: com.f0x1d.logfox.model.LogLine) = allowedLevels.contains(logLine.level) &&
        uid.equalsOrTrueIfNull(logLine.uid) &&
        pid.equalsOrTrueIfNull(logLine.pid) &&
        tid.equalsOrTrueIfNull(logLine.tid) &&
        packageName.equalsOrTrueIfNull(logLine.packageName ?: "") &&
        tag.equalsOrTrueIfNull(logLine.tag) &&
        content.containsOrTrueIfNull(logLine.content)

private fun String?.equalsOrTrueIfNull(other: String) = if (this == null) true else other == this
private fun String?.containsOrTrueIfNull(other: String) = if (this == null) true else other.contains(this)
