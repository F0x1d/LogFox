package com.f0x1d.logfox.feature.preferences.impl.data.logs

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LogsSettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: LogsSettingsLocalDataSource,
    ) : LogsSettingsRepository {
        override var logsUpdateInterval: Long
            get() = localDataSource.logsUpdateInterval
            set(value) {
                localDataSource.logsUpdateInterval = value
            }

        override val logsUpdateIntervalFlow: Flow<Long>
            get() = localDataSource.logsUpdateIntervalFlow

        override var logsTextSize: Int
            get() = localDataSource.logsTextSize
            set(value) {
                localDataSource.logsTextSize = value
            }

        override val logsTextSizeFlow: Flow<Int>
            get() = localDataSource.logsTextSizeFlow

        override var logsDisplayLimit: Int
            get() = localDataSource.logsDisplayLimit
            set(value) {
                localDataSource.logsDisplayLimit = value
            }

        override val logsDisplayLimitFlow: Flow<Int>
            get() = localDataSource.logsDisplayLimitFlow

        override var logsExpanded: Boolean
            get() = localDataSource.logsExpanded
            set(value) {
                localDataSource.logsExpanded = value
            }

        override var resumeLoggingWithBottomTouch: Boolean
            get() = localDataSource.resumeLoggingWithBottomTouch
            set(value) {
                localDataSource.resumeLoggingWithBottomTouch = value
            }

        override var exportLogsInOriginalFormat: Boolean
            get() = localDataSource.exportLogsInOriginalFormat
            set(value) {
                localDataSource.exportLogsInOriginalFormat = value
            }

        override var showLogDate: Boolean
            get() = localDataSource.showLogDate
            set(value) {
                localDataSource.showLogDate = value
            }

        override var showLogTime: Boolean
            get() = localDataSource.showLogTime
            set(value) {
                localDataSource.showLogTime = value
            }

        override var showLogUid: Boolean
            get() = localDataSource.showLogUid
            set(value) {
                localDataSource.showLogUid = value
            }

        override var showLogPid: Boolean
            get() = localDataSource.showLogPid
            set(value) {
                localDataSource.showLogPid = value
            }

        override var showLogTid: Boolean
            get() = localDataSource.showLogTid
            set(value) {
                localDataSource.showLogTid = value
            }

        override var showLogPackage: Boolean
            get() = localDataSource.showLogPackage
            set(value) {
                localDataSource.showLogPackage = value
            }

        override var showLogTag: Boolean
            get() = localDataSource.showLogTag
            set(value) {
                localDataSource.showLogTag = value
            }

        override var showLogContent: Boolean
            get() = localDataSource.showLogContent
            set(value) {
                localDataSource.showLogContent = value
            }

        override val showLogValues: ShowLogValues
            get() = localDataSource.showLogValues

        override val showLogValuesFlow: Flow<ShowLogValues>
            get() = localDataSource.showLogValuesFlow

        override fun originalOf(
            logLine: LogLine,
            formatDate: (Long) -> String,
            formatTime: (Long) -> String,
        ): String =
            if (exportLogsInOriginalFormat) {
                logLine.originalContent
            } else {
                logLine.formatOriginal(
                    values = showLogValues,
                    formatDate = formatDate,
                    formatTime = formatTime,
                )
            }
    }
