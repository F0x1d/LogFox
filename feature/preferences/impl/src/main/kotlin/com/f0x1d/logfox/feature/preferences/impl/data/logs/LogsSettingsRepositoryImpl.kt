package com.f0x1d.logfox.feature.preferences.impl.data.logs

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.impl.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LogsSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: LogsSettingsLocalDataSource,
) : LogsSettingsRepository {

    override fun logsUpdateInterval(): PreferenceStateFlow<Long> = localDataSource.logsUpdateInterval().asPreferenceStateFlow()

    override fun logsTextSize(): PreferenceStateFlow<Int> = localDataSource.logsTextSize().asPreferenceStateFlow()

    override fun logsDisplayLimit(): PreferenceStateFlow<Int> = localDataSource.logsDisplayLimit().asPreferenceStateFlow()

    override fun logsExpanded(): PreferenceStateFlow<Boolean> = localDataSource.logsExpanded().asPreferenceStateFlow()

    override fun resumeLoggingWithBottomTouch(): PreferenceStateFlow<Boolean> = localDataSource.resumeLoggingWithBottomTouch().asPreferenceStateFlow()

    override fun exportLogsInOriginalFormat(): PreferenceStateFlow<Boolean> = localDataSource.exportLogsInOriginalFormat().asPreferenceStateFlow()

    override fun showLogDate(): PreferenceStateFlow<Boolean> = localDataSource.showLogDate().asPreferenceStateFlow()

    override fun showLogTime(): PreferenceStateFlow<Boolean> = localDataSource.showLogTime().asPreferenceStateFlow()

    override fun showLogUid(): PreferenceStateFlow<Boolean> = localDataSource.showLogUid().asPreferenceStateFlow()

    override fun showLogPid(): PreferenceStateFlow<Boolean> = localDataSource.showLogPid().asPreferenceStateFlow()

    override fun showLogTid(): PreferenceStateFlow<Boolean> = localDataSource.showLogTid().asPreferenceStateFlow()

    override fun showLogPackage(): PreferenceStateFlow<Boolean> = localDataSource.showLogPackage().asPreferenceStateFlow()

    override fun showLogTag(): PreferenceStateFlow<Boolean> = localDataSource.showLogTag().asPreferenceStateFlow()

    override fun showLogContent(): PreferenceStateFlow<Boolean> = localDataSource.showLogContent().asPreferenceStateFlow()
}
