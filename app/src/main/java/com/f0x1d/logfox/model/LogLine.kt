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
    VERBOSE("V", R.color.gray_surface_variant, R.color.gray_on_surface_variant),
    DEBUG("D", R.color.blue_primary, R.color.blue_on_primary),
    INFO("I", R.color.green_primary, R.color.green_on_primary),
    WARNING("W", R.color.yellow_primary, R.color.yellow_on_primary),
    ERROR("E", R.color.red_primary, R.color.red_on_primary),
    FATAL("F", R.color.red_primary, R.color.red_on_primary),
    SILENT("S", R.color.red_primary, R.color.red_on_primary)
}