package com.f0x1d.logfox.feature.logging.presentation.view.loglevel

import androidx.annotation.ColorRes
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.logging.presentation.R

@get:ColorRes
val LogLevel.backgroundColorId: Int get() = when (this) {
    LogLevel.VERBOSE -> R.color.level_verbose_background
    LogLevel.DEBUG -> R.color.level_debug_background
    LogLevel.INFO -> R.color.level_info_background
    LogLevel.WARNING -> R.color.level_warning_background
    LogLevel.ERROR -> R.color.level_error_background
    LogLevel.FATAL -> R.color.level_error_background
    LogLevel.SILENT -> R.color.level_error_background
}

@get:ColorRes
val LogLevel.foregroundColorId: Int get() = when (this) {
    LogLevel.VERBOSE -> R.color.level_verbose_on_background
    LogLevel.DEBUG -> R.color.level_debug_on_background
    LogLevel.INFO -> R.color.level_info_on_background
    LogLevel.WARNING -> R.color.level_warning_on_background
    LogLevel.ERROR -> R.color.level_error_on_background
    LogLevel.FATAL -> R.color.level_error_on_background
    LogLevel.SILENT -> R.color.level_error_on_background
}
