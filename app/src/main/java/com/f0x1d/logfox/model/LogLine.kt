package com.f0x1d.logfox.model

import androidx.annotation.ColorRes
import androidx.annotation.Keep
import com.f0x1d.logfox.R
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
enum class LogLevel(
    val letter: String,
    @ColorRes val backgroundColorId: Int,
    @ColorRes val foregroundColorId: Int
) {
    VERBOSE("V", R.color.level_verbose_background, R.color.level_verbose_on_background),
    DEBUG("D", R.color.level_debug_background, R.color.level_debug_on_background),
    INFO("I", R.color.level_info_background, R.color.level_info_on_background),
    WARNING("W", R.color.level_warning_background, R.color.level_warning_on_background),
    ERROR("E", R.color.level_error_background, R.color.level_error_on_background),
    FATAL("F", R.color.level_error_background, R.color.level_error_on_background),
    SILENT("S", R.color.level_error_background, R.color.level_error_on_background)
}