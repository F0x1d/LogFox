package com.f0x1d.logfox.feature.preferences.impl.data.crashes

import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.fredporciuncula.flow.preferences.Preference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CrashesSettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: CrashesSettingsLocalDataSource,
    ) : CrashesSettingsRepository {
        override var openCrashesOnStartup: Boolean
            get() = localDataSource.openCrashesOnStartup
            set(value) {
                localDataSource.openCrashesOnStartup = value
            }

        override var wrapCrashLogLines: Boolean
            get() = localDataSource.wrapCrashLogLines
            set(value) {
                localDataSource.wrapCrashLogLines = value
            }

        override val crashesSortType: Preference<CrashesSort>
            get() = localDataSource.crashesSortType

        override val crashesSortReversedOrder: Preference<Boolean>
            get() = localDataSource.crashesSortReversedOrder

        override fun updateCrashesSortSettings(
            sortType: CrashesSort,
            sortInReversedOrder: Boolean,
        ) {
            localDataSource.updateCrashesSortSettings(sortType, sortInReversedOrder)
        }

        override fun collectingFor(crashTypeName: String): Boolean = localDataSource.collectingFor(crashTypeName)

        override fun showingNotificationsFor(crashTypeName: String): Boolean = localDataSource.showingNotificationsFor(crashTypeName)

        override var useSeparateNotificationsChannelsForCrashes: Boolean
            get() = localDataSource.useSeparateNotificationsChannelsForCrashes
            set(value) {
                localDataSource.useSeparateNotificationsChannelsForCrashes = value
            }
    }
