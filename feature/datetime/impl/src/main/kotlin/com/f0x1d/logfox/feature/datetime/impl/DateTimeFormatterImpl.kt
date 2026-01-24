package com.f0x1d.logfox.feature.datetime.impl

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

internal class DateTimeFormatterImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : DateTimeFormatter {
    private val dateFormatter by lazy {
        createFormatter(dateTimeSettingsRepository.dateFormat().value)
    }
    private val timeFormatter by lazy {
        createFormatter(dateTimeSettingsRepository.timeFormat().value)
    }

    override fun formatDate(time: Long): String = tryFormatBy(dateFormatter, time)

    override fun formatTime(time: Long): String = tryFormatBy(timeFormatter, time)

    override fun formatForExport(time: Long) = formatDate(time)
        .withReplacedBadSymbolsForFileName + "-" +
        formatTime(time)
            .withReplacedBadSymbolsForFileName

    private fun tryFormatBy(formatter: SimpleDateFormat, time: Long) = try {
        formatter.format(time)
    } catch (e: IllegalArgumentException) {
        context.getString(Strings.error, e.localizedMessage)
    }

    private fun createFormatter(format: String?) = SimpleDateFormat(format, Locale.getDefault())

    private val String.withReplacedBadSymbolsForFileName get() =
        replace(":", "-")
            .replace("[^a-zA-Z0-9\\-]".toRegex(), "_")
}
