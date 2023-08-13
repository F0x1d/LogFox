package com.f0x1d.logfox.model

import androidx.annotation.Keep
import com.f0x1d.logfox.extensions.logsDateFormatted
import com.f0x1d.logfox.extensions.logsTimeFormatted

data class LogLine(
    val id: Long,
    val dateAndTime: Long,
    val uid: String,
    val pid: String,
    val tid: String,
    val packageName: String?,
    val level: LogLevel,
    val tag: String,
    val content: String,

    val logsDateFormatted: String = dateAndTime.logsDateFormatted,
    val logsTimeFormatted: String = dateAndTime.logsTimeFormatted
) {
    val original = buildString {
        append("$logsDateFormatted ")
        append("$logsTimeFormatted ")
        append("$uid ")
        append("$pid ")
        append("$tid ")
        if (packageName != null)
            append("$packageName ")
        append("${level.letter}/$tag: ")
        append(content)
    }
}

@Keep
enum class LogLevel(val letter: String) {
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARNING("W"),
    ERROR("E"),
    FATAL("F"),
    SILENT("S")
}