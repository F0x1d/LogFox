package com.f0x1d.logfox.feature.apps.picker.domain

import com.f0x1d.logfox.feature.apps.picker.InstalledApp

interface FilterAppsUseCase {
    operator fun invoke(
        query: String,
        apps: List<InstalledApp>,
    ): List<InstalledApp>
}
