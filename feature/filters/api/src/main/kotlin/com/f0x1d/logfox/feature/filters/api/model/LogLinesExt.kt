package com.f0x1d.logfox.feature.filters.api.model

import com.f0x1d.logfox.feature.logging.api.model.LogLine

fun LogLine.suits(filters: List<UserFilter>) = listOf(this)
    .filterAndSearch(filters)
    .isNotEmpty()

fun List<LogLine>.filterAndSearch(
    filters: List<UserFilter>,
    query: String? = null,
    caseSensitive: Boolean = true,
) = filterByExtendedFilters(filters).let {
    if (query == null) {
        it
    } else {
        it.filter { logLine ->
            logLine.tag.contains(query, ignoreCase = !caseSensitive) ||
                logLine.content.contains(query, ignoreCase = !caseSensitive)
        }
    }
}

private fun List<LogLine>.filterByExtendedFilters(filters: List<UserFilter>): List<LogLine> {
    if (filters.isEmpty()) return this

    val includingFilters = filters.filter { it.including }
    val excludingFilters = filters.filter { !it.including }

    return filter { logLine ->
        val shouldExclude = excludingFilters.any {
            it.lineSuits(logLine)
        }

        if (shouldExclude) {
            false
        } else {
            includingFilters.run {
                if (isEmpty()) {
                    true
                } else {
                    any {
                        it.lineSuits(logLine)
                    }
                }
            }
        }
    }
}

// Identity fields (uid/pid/tid/packageName) match by exact equality; the free-text fields
// (tag/content) match by their MatchData mode (contains/regex/exact). A null/blank field is ignored.
private fun UserFilter.lineSuits(logLine: LogLine) =
    (allowedLevels.isEmpty() || allowedLevels.contains(logLine.level)) &&
    uid.equalsOrTrueIfNull(logLine.uid) &&
    pid.equalsOrTrueIfNull(logLine.pid) &&
    tid.equalsOrTrueIfNull(logLine.tid) &&
    packageName.equalsOrTrueIfNull(logLine.packageName ?: "") &&
    tag.matches(logLine.tag) &&
    content.matches(logLine.content)

private fun String?.equalsOrTrueIfNull(other: String) = if (this == null) true else other == this
