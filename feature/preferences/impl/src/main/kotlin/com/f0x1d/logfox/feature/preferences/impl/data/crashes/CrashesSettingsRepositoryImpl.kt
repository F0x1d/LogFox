package com.f0x1d.logfox.feature.preferences.impl.data.crashes

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CrashesSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: CrashesSettingsLocalDataSource,
) : CrashesSettingsRepository {

    override fun openCrashesOnStartup(): PreferenceStateFlow<Boolean> =
        localDataSource.openCrashesOnStartup().asPreferenceStateFlow()

    override fun wrapCrashLogLines(): PreferenceStateFlow<Boolean> =
        localDataSource.wrapCrashLogLines().asPreferenceStateFlow()

    override fun crashesSortType(): PreferenceStateFlow<CrashesSort> =
        localDataSource.crashesSortType().asPreferenceStateFlow()

    override fun crashesSortReversedOrder(): PreferenceStateFlow<Boolean> =
        localDataSource.crashesSortReversedOrder().asPreferenceStateFlow()

    override fun collectingFor(crashTypeName: String): Boolean =
        localDataSource.collectingFor(crashTypeName)

    override fun showingNotificationsFor(crashTypeName: String): Boolean =
        localDataSource.showingNotificationsFor(crashTypeName)

    override fun useSeparateNotificationsChannelsForCrashes(): PreferenceStateFlow<Boolean> =
        localDataSource.useSeparateNotificationsChannelsForCrashes().asPreferenceStateFlow()
}
