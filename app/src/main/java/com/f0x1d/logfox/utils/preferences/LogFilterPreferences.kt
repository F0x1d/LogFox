package com.f0x1d.logfox.utils.preferences

import android.content.Context
import com.f0x1d.logfox.utils.preferences.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LogFilterPreferences @Inject constructor(@ApplicationContext context: Context): BasePreferences(context) {

    val currentEnabledLogLevels
        get() = EnabledLogLevels(verboseEnabled, debugEnabled, infoEnabled, warningEnabled, errorEnabled, fatalEnabled, silentEnabled)

    var verboseEnabled
        get() = get("pref_enabled_log_verbose", true)
        set(value) { put("pref_enabled_log_verbose", value) }
    var debugEnabled
        get() = get("pref_enabled_log_debug", true)
        set(value) { put("pref_enabled_log_debug", value) }
    var infoEnabled
        get() = get("pref_enabled_log_info", true)
        set(value) { put("pref_enabled_log_info", value) }
    var warningEnabled
        get() = get("pref_enabled_log_warning", true)
        set(value) { put("pref_enabled_log_warning", value) }
    var errorEnabled
        get() = get("pref_enabled_log_error", true)
        set(value) { put("pref_enabled_log_error", value) }
    var fatalEnabled
        get() = get("pref_enabled_log_fatal", true)
        set(value) { put("pref_enabled_log_fatal", value) }
    var silentEnabled
        get() = get("pref_enabled_log_silent", true)
        set(value) { put("pref_enabled_log_silent", value) }

    override fun providePreferences(context: Context) = context.getSharedPreferences("log_filter", Context.MODE_PRIVATE)
}

data class EnabledLogLevels(var verboseEnabled: Boolean,
                            var debugEnabled: Boolean,
                            var infoEnabled: Boolean,
                            var warningEnabled: Boolean,
                            var errorEnabled: Boolean,
                            var fatalEnabled: Boolean,
                            var silentEnabled: Boolean) {
    val checkedItems get() = booleanArrayOf(verboseEnabled, debugEnabled, infoEnabled, warningEnabled, errorEnabled, fatalEnabled, silentEnabled)
}