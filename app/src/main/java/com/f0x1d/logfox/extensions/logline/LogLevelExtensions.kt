package com.f0x1d.logfox.extensions.logline

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.f0x1d.logfox.R
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLevel.*

@ColorRes
fun LogLevel.backgroundColorIdByLevel() = when (this) {
    VERBOSE -> R.color.gray_surface_variant
    INFO -> R.color.green_container
    DEBUG -> R.color.blue_container
    WARNING -> R.color.yellow_container
    ERROR, FATAL, SILENT -> R.color.red_primary
}

@ColorInt
fun LogLevel.backgroundColorByLevel(context: Context, levelColorCacheMap: MutableMap<Int, Int>): Int {
    return backgroundColorIdByLevel().let { levelColorCacheMap.getOrPut(it) { context.getColor(it) }}
}

@ColorInt
fun LogLevel.foregroundColorByLevel(context: Context, levelColorCacheMap: MutableMap<Int, Int>): Int {
    return foregroundColorIdByLevel().let { levelColorCacheMap.getOrPut(it) { context.getColor(it) }}
}

@ColorRes
fun LogLevel.foregroundColorIdByLevel() = when (this) {
    VERBOSE -> R.color.gray_on_surface_variant
    INFO -> R.color.green_on_container
    DEBUG -> R.color.blue_on_container
    WARNING -> R.color.yellow_on_container
    ERROR, FATAL, SILENT -> R.color.red_on_primary
}