package com.f0x1d.logfox.utils

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
): SharedPreferences.OnSharedPreferenceChangeListener {

    private var dateFormatter = createDateFormatter()
    private var timeFormatter = createTimeFormatter()

    fun formatDate(time: Long) = tryFormatBy(dateFormatter, time)
    fun formatTime(time: Long) = tryFormatBy(timeFormatter, time)

    private fun tryFormatBy(formatter: SimpleDateFormat, time: Long) = try {
        formatter.format(time)
    } catch (e: IllegalArgumentException) {
        context.getString(R.string.error, e.localizedMessage)
    }

    fun formatForExport(time: Long) = dateFormatter.format(
        time
    ).withReplacedBadSymbolsForFileName + "-" + timeFormatter.format(
        time
    ).withReplacedBadSymbolsForFileName

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "pref_date_format" -> dateFormatter = createDateFormatter()
            "pref_time_format" -> timeFormatter = createTimeFormatter()
        }
    }

    fun startListening() = appPreferences.registerListener(this)
    fun stopListening() = appPreferences.unregisterListener(this)

    private fun createDateFormatter() = createFormatter(appPreferences.dateFormat)
    private fun createTimeFormatter() = createFormatter(appPreferences.timeFormat)

    private fun createFormatter(format: String?) = SimpleDateFormat(format, Locale.getDefault())

    private val String.withReplacedBadSymbolsForFileName get() = replace(
        ":",
        "-"
    ).replace(
        "[^a-zA-Z0-9\\-]".toRegex(),
        "_"
    )
}