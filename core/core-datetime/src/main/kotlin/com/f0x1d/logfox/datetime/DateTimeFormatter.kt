package com.f0x1d.logfox.datetime

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class DateTimeFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
) {

    private var dateFormatter = createDateFormatter()
    private var timeFormatter = createTimeFormatter()

    fun formatDate(time: Long) = tryFormatBy(dateFormatter, time)
    fun formatTime(time: Long) = tryFormatBy(timeFormatter, time)

    private fun tryFormatBy(formatter: SimpleDateFormat, time: Long) = try {
        formatter.format(time)
    } catch (e: IllegalArgumentException) {
        context.getString(Strings.error, e.localizedMessage)
    }

    fun formatForExport(time: Long) = dateFormatter.format(time)
        .withReplacedBadSymbolsForFileName + "-" + timeFormatter.format(time)
            .withReplacedBadSymbolsForFileName

    private fun createDateFormatter() = createFormatter(appPreferences.dateFormat)
    private fun createTimeFormatter() = createFormatter(appPreferences.timeFormat)

    private fun createFormatter(format: String?) = SimpleDateFormat(format, Locale.getDefault())

    private val String.withReplacedBadSymbolsForFileName get() = replace(":", "-")
        .replace("[^a-zA-Z0-9\\-]".toRegex(), "_")
}
