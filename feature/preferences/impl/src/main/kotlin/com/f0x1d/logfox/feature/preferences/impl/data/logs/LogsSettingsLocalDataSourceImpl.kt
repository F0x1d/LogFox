package com.f0x1d.logfox.feature.preferences.impl.data.logs

import android.content.Context
import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LogsSettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : BasePreferences(context),
    LogsSettingsLocalDataSource {

    override fun logsUpdateInterval(): Preference<Long> = longPreference(
        key = KEY_LOGS_UPDATE_INTERVAL,
        defaultValue = LogsSettingsRepository.LOGS_UPDATE_INTERVAL_DEFAULT,
    )

    override fun logsTextSize(): Preference<Int> = intPreference(
        key = KEY_LOGS_TEXT_SIZE,
        defaultValue = LogsSettingsRepository.LOGS_TEXT_SIZE_DEFAULT,
    )

    override fun logsDisplayLimit(): Preference<Int> = intPreference(
        key = KEY_LOGS_DISPLAY_LIMIT,
        defaultValue = LogsSettingsRepository.LOGS_DISPLAY_LIMIT_DEFAULT,
    )

    override fun logsExpanded(): Preference<Boolean> = booleanPreference(
        key = KEY_LOGS_EXPANDED,
        defaultValue = LogsSettingsRepository.LOGS_EXPANDED_DEFAULT,
    )

    override fun resumeLoggingWithBottomTouch(): Preference<Boolean> = booleanPreference(
        key = KEY_RESUME_LOGS_WITH_TOUCH,
        defaultValue = true,
    )

    override fun exportLogsInOriginalFormat(): Preference<Boolean> = booleanPreference(
        key = KEY_EXPORT_LOGS_IN_ORIGINAL_FORMAT,
        defaultValue = true,
    )

    override fun showLogDate(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_DATE,
        defaultValue = false,
    )

    override fun showLogTime(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_TIME,
        defaultValue = false,
    )

    override fun showLogUid(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_UID,
        defaultValue = false,
    )

    override fun showLogPid(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_PID,
        defaultValue = false,
    )

    override fun showLogTid(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_TID,
        defaultValue = false,
    )

    override fun showLogPackage(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_PACKAGE,
        defaultValue = false,
    )

    override fun showLogTag(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_TAG,
        defaultValue = true,
    )

    override fun showLogContent(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOG_CONTENT,
        defaultValue = true,
    )

    private companion object {
        const val KEY_LOGS_UPDATE_INTERVAL = "pref_logs_update_interval"
        const val KEY_LOGS_TEXT_SIZE = "pref_logs_text_size"
        const val KEY_LOGS_DISPLAY_LIMIT = "pref_logs_display_limit"
        const val KEY_LOGS_EXPANDED = "pref_logs_expanded"
        const val KEY_RESUME_LOGS_WITH_TOUCH = "pref_resume_logs_with_touch"
        const val KEY_EXPORT_LOGS_IN_ORIGINAL_FORMAT = "pref_export_logs_in_original_format"
        const val KEY_SHOW_LOG_DATE = "pref_show_log_date"
        const val KEY_SHOW_LOG_TIME = "pref_show_log_time"
        const val KEY_SHOW_LOG_UID = "pref_show_log_uid"
        const val KEY_SHOW_LOG_PID = "pref_show_log_pid"
        const val KEY_SHOW_LOG_TID = "pref_show_log_tid"
        const val KEY_SHOW_LOG_PACKAGE = "pref_show_log_package"
        const val KEY_SHOW_LOG_TAG = "pref_show_log_tag"
        const val KEY_SHOW_LOG_CONTENT = "pref_show_log_content"
    }
}
