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
    LogLevel.VERBOSE -> Color.WHITE
    LogLevel.INFO -> Color.WHITE
    LogLevel.DEBUG -> Color.WHITE
    LogLevel.WARNING -> Color.BLACK
    LogLevel.ERROR, LogLevel.FATAL, LogLevel.SILENT -> Color.WHITE
}