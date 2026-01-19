package com.f0x1d.logfox.feature.datetime.api

interface DateTimeFormatter {
    fun formatDate(time: Long): String
    fun formatTime(time: Long): String

    fun formatForExport(time: Long): String
}
