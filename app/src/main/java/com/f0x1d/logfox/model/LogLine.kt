package com.f0x1d.logfox.model

import androidx.annotation.Keep
import com.f0x1d.logfox.extensions.logsDateFormatted
import com.f0x1d.logfox.extensions.logsTimeFormatted

data class LogLine(
    val id: Long,
    val dateAndTime: Long = System.currentTimeMillis(),
    val uid: String = "",
    val pid: String = "",
    val tid: String = "",
    val packageName: String? = null,
    val level: LogLevel = LogLevel.INFO,
    val tag: String = "",
    val content: String,
    val original: String,

    val logsDateFormatted: String = dateAndTime.logsDateFormatted,
    val logsTimeFormatted: String = dateAndTime.logsTimeFormatted
)

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