package com.f0x1d.logfox.extensions

import android.graphics.Color
import com.f0x1d.logfox.model.LogLevel

fun LogLevel.backgroundColorByLevel() = when (this) {
    LogLevel.VERBOSE -> Color.GRAY
    LogLevel.INFO -> Color.GREEN
    LogLevel.DEBUG -> Color.BLUE
    LogLevel.WARNING -> Color.YELLOW
    LogLevel.ERROR, LogLevel.FATAL, LogLevel.SILENT -> Color.RED
}

fun LogLevel.foregroundColorByLevel() = when (this) {
    LogLevel.VERBOSE, LogLevel.DEBUG, LogLevel.ERROR, LogLevel.FATAL, LogLevel.SILENT -> Color.WHITE
    LogLevel.INFO, LogLevel.WARNING -> Color.BLACK
}