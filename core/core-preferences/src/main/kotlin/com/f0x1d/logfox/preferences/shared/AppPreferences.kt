package com.f0x1d.logfox.preferences.shared

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.model.preferences.ShowLogValues
import com.f0x1d.logfox.preferences.shared.base.BasePreferences
import com.fredporciuncula.flow.preferences.keyFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
): BasePreferences(context) {

    companion object {
        const val DATE_FORMAT_DEFAULT = "dd.MM"
        const val TIME_FORMAT_DEFAULT = "HH:mm:ss.SSS"

        const val LOGS_EXPANDED_DEFAULT = false
        const val LOGS_UPDATE_INTERVAL_DEFAULT = 300L
        const val LOGS_TEXT_SIZE_DEFAULT = 14
        const val LOGS_DISPLAY_LIMIT_DEFAULT = 10000

        const val TERMINAL_INDEX_DEFAULT = 0
    }

    var dateFormat
        get() = getNullable("pref_date_format", DATE_FORMAT_DEFAULT)
        set(value) { put("pref_date_format", value) }
    val dateFormatFlow get() = flowSharedPreferences.getString("pref_date_format", DATE_FORMAT_DEFAULT).asFlow()

    var timeFormat
        get() = getNullable("pref_time_format", TIME_FORMAT_DEFAULT)
        set(value) { put("pref_time_format", value) }
    val timeFormatFlow get() = flowSharedPreferences.getString("pref_time_format", TIME_FORMAT_DEFAULT).asFlow()

    var logsUpdateInterval
        get() = get("pref_logs_update_interval", LOGS_UPDATE_INTERVAL_DEFAULT)
        set(value) { put("pref_logs_update_interval", value) }
    val logsUpdateIntervalFlow get() = flowSharedPreferences.getLong(
        key = "pref_logs_update_interval",
        defaultValue = LOGS_UPDATE_INTERVAL_DEFAULT,
    ).asFlow()

    var logsTextSize
        get() = get("pref_logs_text_size", LOGS_TEXT_SIZE_DEFAULT)
        set(value) { put("pref_logs_text_size", value) }
    val logsTextSizeFlow get() = flowSharedPreferences.getInt("pref_logs_text_size", LOGS_TEXT_SIZE_DEFAULT).asFlow()

    var logsDisplayLimit
        get() = get("pref_logs_display_limit", LOGS_DISPLAY_LIMIT_DEFAULT)
        set(value) { put("pref_logs_display_limit", value) }
    val logsDisplayLimitFlow get() = flowSharedPreferences.getInt(
        key = "pref_logs_display_limit",
        defaultValue = LOGS_DISPLAY_LIMIT_DEFAULT,
    ).asFlow()

    var logsExpanded
        get() = get("pref_logs_expanded", LOGS_EXPANDED_DEFAULT)
        set(value) { put("pref_logs_expanded", value) }
    val logsExpandedFlow get() = flowSharedPreferences.getBoolean("pref_logs_expanded", LOGS_EXPANDED_DEFAULT).asFlow()

    var resumeLoggingWithBottomTouch
        get() = get("pref_resume_logs_with_touch", true)
        set(value) { put("pref_resume_logs_with_touch", value) }
    var exportLogsInOriginalFormat
        get() = get("pref_export_logs_in_original_format", true)
        set(value) { put("pref_export_logs_in_original_format", value) }

    var showLogDate
        get() = get("pref_show_log_date", false)
        set(value) { put("pref_show_log_date", value) }
    var showLogTime
        get() = get("pref_show_log_time", false)
        set(value) { put("pref_show_log_time", value) }
    var showLogUid
        get() = get("pref_show_log_uid", false)
        set(value) { put("pref_show_log_uid", value) }
    var showLogPid
        get() = get("pref_show_log_pid", false)
        set(value) { put("pref_show_log_pid", value) }
    var showLogTid
        get() = get("pref_show_log_tid", false)
        set(value) { put("pref_show_log_tid", value) }
    var showLogPackage
        get() = get("pref_show_log_package", false)
        set(value) { put("pref_show_log_package", value) }
    var showLogTag
        get() = get("pref_show_log_tag", true)
        set(value) { put("pref_show_log_tag", value) }
    var showLogContent
        get() = get("pref_show_log_content", true)
        set(value) { put("pref_show_log_content", value) }

    val showLogValuesFlow get() = sharedPreferences.keyFlow.filter { key ->
        key?.startsWith("pref_show_log") == true
    }.onStart { emit("initial") }.map { showLogValues }

    var selectedTerminalIndex
        get() = get("pref_selected_terminal_index", TERMINAL_INDEX_DEFAULT)
        set(value) { put("pref_selected_terminal_index", value) }
    val selectedTerminalIndexFlow get() = flowSharedPreferences.getInt(
        key = "pref_selected_terminal_index",
        defaultValue = TERMINAL_INDEX_DEFAULT,
    ).asFlow()

    var fallbackToDefaultTerminal
        get() = get("pref_fallback_to_default_terminal", true)
        set(value) { put("pref_fallback_to_default_terminal", value) }
    var startOnBoot
        get() = get("pref_start_on_boot", true)
        set(value) { put("pref_start_on_boot", value) }
    var showLogsFromAppLaunch
        get() = get("pref_show_logs_from_app_launch", true)
        set(value) { put("pref_show_logs_from_app_launch", value) }
    var includeDeviceInfoInArchives
        get() = get("pref_include_device_info_in_archives", true)
        set(value) { put("pref_include_device_info_in_archives", value) }

    var askedNotificationsPermission
        get() = get("pref_asked_notifications_permission", false)
        set(value) { put("pref_asked_notifications_permission", value) }

    val showLogValues get() = ShowLogValues(
        showLogDate,
        showLogTime,
        showLogUid,
        showLogPid,
        showLogTid,
        showLogPackage,
        showLogTag,
        showLogContent,
    )

    fun collectingFor(crashType: CrashType) = get(
        key = "pref_collect_${crashType.readableName.lowercase()}",
        defaultValue = true
    )
    fun showingNotificationsFor(crashType: CrashType) = get(
        key = "pref_notifications_${crashType.readableName.lowercase()}",
        defaultValue = true
    )

    fun selectTerminal(index: Int) = sharedPreferences.edit(commit = true) {
        putInt("pref_selected_terminal_index", index)
    }

    fun originalOf(
        logLine: LogLine,
        formatDate: (Long) -> String = { Date(it).toLocaleString() },
        formatTime: (Long) -> String = { Date(it).toLocaleString() },
    ): String = if (exportLogsInOriginalFormat) {
        logLine.originalContent
    } else {
        logLine.formatOriginal(
            values = showLogValues,
            formatDate = formatDate,
            formatTime = formatTime,
        )
    }

    override fun providePreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
}