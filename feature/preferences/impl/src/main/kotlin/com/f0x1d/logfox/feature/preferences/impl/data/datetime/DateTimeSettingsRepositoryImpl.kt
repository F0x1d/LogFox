package com.f0x1d.logfox.feature.preferences.impl.data.datetime

import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DateTimeSettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: DateTimeSettingsLocalDataSource,
    ) : DateTimeSettingsRepository {
        override var dateFormat: String?
            get() = localDataSource.dateFormat
            set(value) {
                localDataSource.dateFormat = value
            }

        override val dateFormatFlow: Flow<String>
            get() = localDataSource.dateFormatFlow

        override var timeFormat: String?
            get() = localDataSource.timeFormat
            set(value) {
                localDataSource.timeFormat = value
            }

        override val timeFormatFlow: Flow<String>
            get() = localDataSource.timeFormatFlow
    }
