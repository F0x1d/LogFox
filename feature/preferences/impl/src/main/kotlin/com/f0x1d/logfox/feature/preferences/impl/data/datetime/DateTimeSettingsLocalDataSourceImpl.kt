package com.f0x1d.logfox.feature.preferences.impl.data.datetime

import android.content.Context
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DateTimeSettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : BasePreferences(context), DateTimeSettingsLocalDataSource {

    override fun dateFormat(): Preference<String> = stringPreference(
        key = KEY_DATE_FORMAT,
        defaultValue = DateTimeSettingsRepository.DATE_FORMAT_DEFAULT,
    )

    override fun timeFormat(): Preference<String> = stringPreference(
        key = KEY_TIME_FORMAT,
        defaultValue = DateTimeSettingsRepository.TIME_FORMAT_DEFAULT,
    )

    private companion object {
        const val KEY_DATE_FORMAT = "pref_date_format"
        const val KEY_TIME_FORMAT = "pref_time_format"
    }
}
