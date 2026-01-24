package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.CrashesSort

interface CrashesSettingsRepository {
    fun openCrashesOnStartup(): PreferenceStateFlow<Boolean>
    fun wrapCrashLogLines(): PreferenceStateFlow<Boolean>

    fun crashesSortType(): PreferenceStateFlow<CrashesSort>
    fun crashesSortReversedOrder(): PreferenceStateFlow<Boolean>

    fun collectingFor(crashTypeName: String): Boolean
    fun showingNotificationsFor(crashTypeName: String): Boolean

    fun useSeparateNotificationsChannelsForCrashes(): PreferenceStateFlow<Boolean>
}
