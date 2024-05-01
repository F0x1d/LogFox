package com.f0x1d.logfox.model

import androidx.annotation.Keep

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
    val original: String
)

@Keep
enum class LogLevel(val letter: String) {
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARNING("W"),
    ERROR("E"),
    FATAL("F"),
    SILENT("S"),
}
