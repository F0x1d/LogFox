package com.f0x1d.logfox.extensions.logline

import android.content.Context
import com.f0x1d.logfox.R
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLevel.*

fun LogLevel.backgroundColorByLevel(context: Context) = context.getColor(when (this) {
    VERBOSE -> R.color.gray_surface_variant
    INFO -> R.color.green_container
    DEBUG -> R.color.blue_container
    WARNING -> R.color.yellow_container
    ERROR, FATAL, SILENT -> R.color.red_primary
})

fun LogLevel.foregroundColorByLevel(context: Context) =  context.getColor(when (this) {
    VERBOSE -> R.color.gray_on_surface_variant
    INFO -> R.color.green_on_container
    DEBUG -> R.color.blue_on_container
    WARNING -> R.color.yellow_on_container
    ERROR, FATAL, SILENT -> R.color.red_on_primary
})