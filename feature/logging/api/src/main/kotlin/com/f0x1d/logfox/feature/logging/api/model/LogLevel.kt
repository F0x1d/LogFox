package com.f0x1d.logfox.feature.logging.api.model

import androidx.annotation.Keep

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
