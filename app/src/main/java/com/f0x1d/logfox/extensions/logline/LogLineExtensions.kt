package com.f0x1d.logfox.extensions.logline

import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLine

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