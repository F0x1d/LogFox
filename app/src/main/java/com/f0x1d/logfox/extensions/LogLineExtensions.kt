package com.f0x1d.logfox.extensions

import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.FiltersRepository

fun List<LogLine>.filterAndSearch(filtersRepository: FiltersRepository, query: String?) = filterByExtendedFilters(filtersRepository.filtersFlow.value.filter { it.enabled })
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

private val logRegex = "(.{23}) (.{5}) (.{5}) (.) (.+?): (.+)".toRegex()

fun LogLine(id: Long, line: String) = logRegex.find(line)?.run {
    LogLine(
        id,
        groupValues[1].replace(" ", "").run {
            indexOf(".").let {
                substring(0, it).toLong() * 1000 + substring(it + 1).toLong()
            }
        },
        groupValues[2].replace(" ", ""),
        groupValues[3].replace(" ", ""),
        mapLevel(groupValues[4]),
        groupValues[5],
        groupValues[6]
    )
}

private fun mapLevel(level: String) = LogLevel.values().find { it.letter == level } ?: throw RuntimeException("wtf is $level")