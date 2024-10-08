package com.f0x1d.logfox.datetime

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

interface DateTimeFormatter {
    fun formatDate(time: Long): String
    fun formatTime(time: Long): String

    fun formatForExport(time: Long): String
}

internal class DateTimeFormatterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
) : DateTimeFormatter {

    private val dateFormatter by lazy { createFormatter(appPreferences.dateFormat) }
    private val timeFormatter by lazy { createFormatter(appPreferences.timeFormat) }

    override fun formatDate(time: Long): String = tryFormatBy(dateFormatter, time)
    override fun formatTime(time: Long): String = tryFormatBy(timeFormatter, time)

    override fun formatForExport(time: Long) = formatDate(time)
        .withReplacedBadSymbolsForFileName + "-" + formatTime(time)
            .withReplacedBadSymbolsForFileName

    private fun tryFormatBy(formatter: SimpleDateFormat, time: Long) = try {
        formatter.format(time)
    } catch (e: IllegalArgumentException) {
        context.getString(Strings.error, e.localizedMessage)
    }

    private fun createFormatter(format: String?) = SimpleDateFormat(format, Locale.getDefault())

    private val String.withReplacedBadSymbolsForFileName get() = replace(":", "-")
        .replace("[^a-zA-Z0-9\\-]".toRegex(), "_")
}
