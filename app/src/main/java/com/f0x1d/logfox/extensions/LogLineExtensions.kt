package com.f0x1d.logfox.extensions

import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.utils.preferences.EnabledLogLevels

fun List<LogLine>.filterAndSearch(query: String?, currentEnabledLogLevels: EnabledLogLevels) = filterEnabledLines(currentEnabledLogLevels).let {
    if (query == null)
        it
    else
        it.filter { it.tag.contains(query) || it.content.contains(query) }
}

fun List<LogLine>.filterEnabledLines(currentEnabledLogLevels: EnabledLogLevels) = filter {
    when {
        currentEnabledLogLevels.verboseEnabled && it.level == LogLevel.VERBOSE -> true
        currentEnabledLogLevels.debugEnabled && it.level == LogLevel.DEBUG -> true
        currentEnabledLogLevels.infoEnabled && it.level == LogLevel.INFO -> true
        currentEnabledLogLevels.warningEnabled && it.level == LogLevel.WARNING -> true
        currentEnabledLogLevels.errorEnabled && it.level == LogLevel.ERROR -> true
        currentEnabledLogLevels.fatalEnabled && it.level == LogLevel.FATAL -> true
        currentEnabledLogLevels.silentEnabled && it.level == LogLevel.SILENT -> true
        else -> false
    }
}

private val logRegex = "(.{23}) (.{5}) (.{5}) (.) (.+?): (.+)".toRegex()

fun LogLine(id: Long, line: String): LogLine? {
    val result = logRegex.find(line) ?: return null

    return LogLine(
        id,
        result.groupValues[1].replace(" ", "").run {
            indexOf(".").let {
                substring(0, it).toLong() * 1000 + substring(it + 1).toLong()
            }
        },
        result.groupValues[2].replace(" ", ""),
        result.groupValues[3].replace(" ", ""),
        mapLevel(result.groupValues[4]),
        result.groupValues[5],
        result.groupValues[6]
    )
}

private fun mapLevel(level: String) = LogLevel.values().find { it.letter == level } ?: throw RuntimeException("wtf is $level")