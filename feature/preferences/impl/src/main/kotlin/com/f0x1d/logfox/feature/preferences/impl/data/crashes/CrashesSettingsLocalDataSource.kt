package com.f0x1d.logfox.feature.preferences.impl.data.crashes

import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.fredporciuncula.flow.preferences.Preference

internal interface CrashesSettingsLocalDataSource {
    var openCrashesOnStartup: Boolean
    var wrapCrashLogLines: Boolean

    val crashesSortType: Preference<CrashesSort>
    val crashesSortReversedOrder: Preference<Boolean>

    fun updateCrashesSortSettings(
        sortType: CrashesSort,
        sortInReversedOrder: Boolean,
    )

    fun collectingFor(crashTypeName: String): Boolean

    fun showingNotificationsFor(crashTypeName: String): Boolean

    var useSeparateNotificationsChannelsForCrashes: Boolean
}
