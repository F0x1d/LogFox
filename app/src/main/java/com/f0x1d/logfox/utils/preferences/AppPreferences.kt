package com.f0x1d.logfox.utils.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.utils.preferences.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferences @Inject constructor(@ApplicationContext context: Context): BasePreferences(context) {

    var startOnBoot
        get() = get("pref_start_on_boot", true)
        set(value) { put("pref_start_on_boot", value) }
    var logsUpdateInterval
        get() = get("pref_logs_update_interval", 150L)
        set(value) { put("pref_logs_update_interval", value) }
    var logsTextSize
        get() = get("pref_logs_text_size", 14)
        set(value) { put("pref_logs_text_size", value) }

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

    val showLogValues get() = booleanArrayOf(showLogTime, showLogPid, showLogTid, showLogTag, showLogContent)

    fun collectingFor(crashType: CrashType) = get("pref_collect_${crashType.readableName.lowercase()}", true)
    fun showingNotificationsFor(crashType: CrashType) = get("pref_notifications_${crashType.readableName.lowercase()}", true)

    override fun providePreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
}