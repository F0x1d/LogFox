package com.f0x1d.logfox.feature.datetime.impl

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.preferences.api.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class DateTimeFormatterImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : DateTimeFormatter {

    private val formatters = ConcurrentHashMap<String, SimpleDateFormat>()

    override fun formatDate(time: Long): String = tryFormatBy(
        formatterFor(dateTimeSettingsRepository.dateFormat().value),
        time,
    )

    override fun formatTime(time: Long): String = tryFormatBy(
        formatterFor(dateTimeSettingsRepository.timeFormat().value),
        time,
    )

    override fun formatForExport(time: Long) = formatDate(time)
        .withReplacedBadSymbolsForFileName + "-" +
        formatTime(time)
            .withReplacedBadSymbolsForFileName

    private fun tryFormatBy(formatter: SimpleDateFormat, time: Long) = try {
        formatter.format(time)
    } catch (e: IllegalArgumentException) {
        context.getString(Strings.error, e.localizedMessage)
    }

    private fun formatterFor(format: String?): SimpleDateFormat = formatters.getOrPut(format.orEmpty()) {
        try {
            SimpleDateFormat(format, Locale.getDefault())
        } catch (e: RuntimeException) {
            SimpleDateFormat(
                DateTimeSettingsRepository.DATE_FORMAT_DEFAULT,
                Locale.getDefault(),
            )
        }
    }

    private val String.withReplacedBadSymbolsForFileName get() =
        replace(":", "-")
            .replace("[^a-zA-Z0-9\\-]".toRegex(), "_")
}
