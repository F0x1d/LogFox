package com.f0x1d.logfox.extensions

import android.graphics.Color
import com.f0x1d.logfox.model.LogLevel
import com.google.android.material.color.MaterialColors

fun LogLevel.backgroundColorByLevel(primaryColor: Int) = when (this) {
    LogLevel.VERBOSE -> MaterialColors.harmonize(Color.GRAY, primaryColor)
    LogLevel.INFO -> MaterialColors.harmonize(Color.GREEN, primaryColor)
    LogLevel.DEBUG -> MaterialColors.harmonize(Color.BLUE, primaryColor)
    LogLevel.WARNING -> MaterialColors.harmonize(Color.YELLOW, primaryColor)
    LogLevel.ERROR, LogLevel.FATAL, LogLevel.SILENT -> MaterialColors.harmonize(Color.RED, primaryColor)
}

fun LogLevel.foregroundColorByLevel() = when (this) {
    LogLevel.VERBOSE -> Color.WHITE
    LogLevel.INFO -> Color.WHITE
    LogLevel.DEBUG -> Color.WHITE
    LogLevel.WARNING -> Color.BLACK
    LogLevel.ERROR, LogLevel.FATAL, LogLevel.SILENT -> Color.WHITE
}