package com.f0x1d.logfox.utils.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.utils.preferences.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext context: Context): BasePreferences(context) {

    companion object {
        const val LOGS_UPDATE_INTERVAL_DEFAULT = 300L
        const val LOGS_TEXT_SIZE_DEFAULT = 14
        const val LOGS_DISPLAY_LIMIT_DEFAULT = 10000
    }

    var startOnBoot
        get() = get("pref_start_on_boot", true)
        set(value) { put("pref_start_on_boot", value) }
    var startOnLaunch
        get() = get("pref_start_on_launch", true)
        set(value) { put("pref_start_on_launch", value) }
    var selectedTerminalIndex
        get() = get("pref_selected_terminal_index", 0)
        set(value) { put("pref_selected_terminal_index", value) }

    var nightTheme
        get() = get("pref_night_theme", 0)
        set(value) { put("pref_night_theme", value) }
    var logsUpdateInterval
        get() = get("pref_logs_update_interval", LOGS_UPDATE_INTERVAL_DEFAULT)
        set(value) { put("pref_logs_update_interval", value) }
    var logsTextSize
        get() = get("pref_logs_text_size", LOGS_TEXT_SIZE_DEFAULT)
        set(value) { put("pref_logs_text_size", value) }
    var logsDisplayLimit
        get() = get("pref_logs_display_limit", LOGS_DISPLAY_LIMIT_DEFAULT)
        set(value) { put("pref_logs_display_limit", value) }
    var logsExpanded
        get() = get("pref_logs_expanded", false)
        set(value) { put("pref_logs_expanded", value) }
    var resumeLoggingWithBottomTouch
        get() = get("pref_resume_logs_with_touch", true)
        set(value) { put("pref_resume_logs_with_touch", value) }

    var showLogDate
        get() = get("pref_show_log_date", false)
        set(value) { put("pref_show_log_date", value) }
    var showLogTime
        get() = get("pref_show_log_time", false)
        set(value) { put("pref_show_log_time", value) }
    var showLogPid
        get() = get("pref_show_log_pid", false)
        set(value) { put("pref_show_log_pid", value) }
    var showLogTid
        get() = get("pref_show_log_tid", false)
        set(value) { put("pref_show_log_tid", value) }
    var showLogTag
        get() = get("pref_show_log_tag", true)
        set(value) { put("pref_show_log_tag", value) }
    var showLogContent
        get() = get("pref_show_log_content", true)
        set(value) { put("pref_show_log_content", value) }

    var askedNotificationsPermission
        get() = get("pref_asked_notifications_permission", false)
        set(value) { put("pref_asked_notifications_permission", value) }

    var showStartServiceNotificationOnBoot
        get() = get("pref_show_start_service_reminder_notification_on_boot", false)
        set(value) { put("pref_show_start_service_reminder_notification_on_boot", value) }

    val showLogValues get() = booleanArrayOf(showLogDate, showLogTime, showLogPid, showLogTid, showLogTag, showLogContent)

    fun collectingFor(crashType: CrashType) = get("pref_collect_${crashType.readableName.lowercase()}", true)
    fun showingNotificationsFor(crashType: CrashType) = get("pref_notifications_${crashType.readableName.lowercase()}", true)

    override fun providePreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
}