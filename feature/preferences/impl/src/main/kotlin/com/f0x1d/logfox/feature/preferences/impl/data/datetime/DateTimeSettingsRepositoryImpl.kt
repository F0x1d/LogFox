package com.f0x1d.logfox.feature.preferences.impl.data.datetime

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DateTimeSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: DateTimeSettingsLocalDataSource,
) : DateTimeSettingsRepository {

    override fun dateFormat(): PreferenceStateFlow<String> =
        localDataSource.dateFormat().asPreferenceStateFlow()

    override fun timeFormat(): PreferenceStateFlow<String> =
        localDataSource.timeFormat().asPreferenceStateFlow()
}
