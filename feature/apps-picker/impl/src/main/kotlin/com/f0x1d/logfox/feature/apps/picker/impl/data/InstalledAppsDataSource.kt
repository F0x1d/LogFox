package com.f0x1d.logfox.feature.apps.picker.impl.data

import com.f0x1d.logfox.feature.apps.picker.InstalledApp

internal interface InstalledAppsDataSource {
    fun getInstalledApps(): List<InstalledApp>
}
