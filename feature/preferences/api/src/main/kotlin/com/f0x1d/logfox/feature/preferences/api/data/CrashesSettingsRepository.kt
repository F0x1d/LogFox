package com.f0x1d.logfox.feature.preferences.api.data

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.api.CrashesSort

interface CrashesSettingsRepository {
    fun openCrashesOnStartup(): PreferenceStateFlow<Boolean>
    fun wrapCrashLogLines(): PreferenceStateFlow<Boolean>

    fun crashesSortType(): PreferenceStateFlow<CrashesSort>
    fun crashesSortReversedOrder(): PreferenceStateFlow<Boolean>

    fun collectingFor(crashTypeName: String): Boolean
    fun showingNotificationsFor(crashTypeName: String): Boolean

    fun useSeparateNotificationsChannelsForCrashes(): PreferenceStateFlow<Boolean>
}
