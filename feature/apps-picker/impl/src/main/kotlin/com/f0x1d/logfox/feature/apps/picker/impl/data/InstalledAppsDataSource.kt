package com.f0x1d.logfox.feature.apps.picker.impl.data

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import kotlinx.collections.immutable.ImmutableList

internal interface InstalledAppsDataSource {
    fun getInstalledApps(): ImmutableList<InstalledApp>
}
