package com.f0x1d.logfox.feature.preferences.impl.data.datetime

import android.content.Context
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DateTimeSettingsLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : BasePreferences(context),
        DateTimeSettingsLocalDataSource {
        override var dateFormat
            get() = getNullable(KEY_DATE_FORMAT, DateTimeSettingsRepository.DATE_FORMAT_DEFAULT)
            set(value) = put(KEY_DATE_FORMAT, value)

        override val dateFormatFlow get() =
            flowSharedPreferences
                .getString(
                    KEY_DATE_FORMAT,
                    DateTimeSettingsRepository.DATE_FORMAT_DEFAULT,
                ).asFlow()

        override var timeFormat
            get() = getNullable(KEY_TIME_FORMAT, DateTimeSettingsRepository.TIME_FORMAT_DEFAULT)
            set(value) = put(KEY_TIME_FORMAT, value)

        override val timeFormatFlow get() =
            flowSharedPreferences
                .getString(
                    KEY_TIME_FORMAT,
                    DateTimeSettingsRepository.TIME_FORMAT_DEFAULT,
                ).asFlow()

        private companion object {
            const val KEY_DATE_FORMAT = "pref_date_format"
            const val KEY_TIME_FORMAT = "pref_time_format"
        }
    }
