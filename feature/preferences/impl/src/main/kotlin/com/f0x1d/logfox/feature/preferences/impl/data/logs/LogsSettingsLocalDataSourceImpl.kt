package com.f0x1d.logfox.feature.preferences.impl.data.logs

import android.content.Context
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LogsSettingsLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : BasePreferences(context),
        LogsSettingsLocalDataSource {
        private val _showLogValuesFlow = MutableStateFlow(createShowLogValues())
        override val showLogValuesFlow: Flow<ShowLogValues> = _showLogValuesFlow.asStateFlow()

        override var logsUpdateInterval
            get() = get(KEY_LOGS_UPDATE_INTERVAL, LogsSettingsRepository.LOGS_UPDATE_INTERVAL_DEFAULT)
            set(value) = put(KEY_LOGS_UPDATE_INTERVAL, value)

        override val logsUpdateIntervalFlow get() =
            flowSharedPreferences
                .getLong(
                    key = KEY_LOGS_UPDATE_INTERVAL,
                    defaultValue = LogsSettingsRepository.LOGS_UPDATE_INTERVAL_DEFAULT,
                ).asFlow()

        override var logsTextSize
            get() = get(KEY_LOGS_TEXT_SIZE, LogsSettingsRepository.LOGS_TEXT_SIZE_DEFAULT)
            set(value) = put(KEY_LOGS_TEXT_SIZE, value)

        override val logsTextSizeFlow get() =
            flowSharedPreferences
                .getInt(
                    KEY_LOGS_TEXT_SIZE,
                    LogsSettingsRepository.LOGS_TEXT_SIZE_DEFAULT,
                ).asFlow()

        override var logsDisplayLimit
            get() = get(KEY_LOGS_DISPLAY_LIMIT, LogsSettingsRepository.LOGS_DISPLAY_LIMIT_DEFAULT)
            set(value) = put(KEY_LOGS_DISPLAY_LIMIT, value)

        override val logsDisplayLimitFlow get() =
            flowSharedPreferences
                .getInt(
                    key = KEY_LOGS_DISPLAY_LIMIT,
                    defaultValue = LogsSettingsRepository.LOGS_DISPLAY_LIMIT_DEFAULT,
                ).asFlow()

        override var logsExpanded
            get() = get(KEY_LOGS_EXPANDED, LogsSettingsRepository.LOGS_EXPANDED_DEFAULT)
            set(value) = put(KEY_LOGS_EXPANDED, value)

        override var resumeLoggingWithBottomTouch
            get() = get(KEY_RESUME_LOGS_WITH_TOUCH, true)
            set(value) = put(KEY_RESUME_LOGS_WITH_TOUCH, value)

        override var exportLogsInOriginalFormat
            get() = get(KEY_EXPORT_LOGS_IN_ORIGINAL_FORMAT, true)
            set(value) = put(KEY_EXPORT_LOGS_IN_ORIGINAL_FORMAT, value)

        override var showLogDate
            get() = get(KEY_SHOW_LOG_DATE, false)
            set(value) {
                put(KEY_SHOW_LOG_DATE, value)
                updateShowLogValuesFlow()
            }

        override var showLogTime
            get() = get(KEY_SHOW_LOG_TIME, false)
            set(value) {
                put(KEY_SHOW_LOG_TIME, value)
                updateShowLogValuesFlow()
            }

        override var showLogUid
            get() = get(KEY_SHOW_LOG_UID, false)
            set(value) {
                put(KEY_SHOW_LOG_UID, value)
                updateShowLogValuesFlow()
            }

        override var showLogPid
            get() = get(KEY_SHOW_LOG_PID, false)
            set(value) {
                put(KEY_SHOW_LOG_PID, value)
                updateShowLogValuesFlow()
            }

        override var showLogTid
            get() = get(KEY_SHOW_LOG_TID, false)
            set(value) {
                put(KEY_SHOW_LOG_TID, value)
                updateShowLogValuesFlow()
            }

        override var showLogPackage
            get() = get(KEY_SHOW_LOG_PACKAGE, false)
            set(value) {
                put(KEY_SHOW_LOG_PACKAGE, value)
                updateShowLogValuesFlow()
            }

        override var showLogTag
            get() = get(KEY_SHOW_LOG_TAG, true)
            set(value) {
                put(KEY_SHOW_LOG_TAG, value)
                updateShowLogValuesFlow()
            }

        override var showLogContent
            get() = get(KEY_SHOW_LOG_CONTENT, true)
            set(value) {
                put(KEY_SHOW_LOG_CONTENT, value)
                updateShowLogValuesFlow()
            }

        override val showLogValues: ShowLogValues get() = _showLogValuesFlow.value

        private fun updateShowLogValuesFlow() {
            _showLogValuesFlow.value = createShowLogValues()
        }

        private fun createShowLogValues(): ShowLogValues =
            ShowLogValues(
                showLogDate,
                showLogTime,
                showLogUid,
                showLogPid,
                showLogTid,
                showLogPackage,
                showLogTag,
                showLogContent,
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
