package com.f0x1d.logfox.extensions.logline

import androidx.annotation.ColorRes
import com.f0x1d.logfox.R
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLevel.DEBUG
import com.f0x1d.logfox.model.LogLevel.ERROR
import com.f0x1d.logfox.model.LogLevel.FATAL
import com.f0x1d.logfox.model.LogLevel.INFO
import com.f0x1d.logfox.model.LogLevel.SILENT
import com.f0x1d.logfox.model.LogLevel.VERBOSE
import com.f0x1d.logfox.model.LogLevel.WARNING

@ColorRes
fun LogLevel.backgroundColorIdByLevel() = when (this) {
    VERBOSE -> R.color.gray_surface_variant
    INFO -> R.color.green_primary
    DEBUG -> R.color.blue_primary
    WARNING -> R.color.yellow_primary
    ERROR, FATAL, SILENT -> R.color.red_primary
}

@ColorRes
fun LogLevel.foregroundColorIdByLevel() = when (this) {
    VERBOSE -> R.color.gray_on_surface_variant
    INFO -> R.color.green_on_primary
    DEBUG -> R.color.blue_on_primary
    WARNING -> R.color.yellow_on_primary
    ERROR, FATAL, SILENT -> R.color.red_on_primary
}