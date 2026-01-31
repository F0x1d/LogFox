package com.f0x1d.logfox.feature.apps.picker.api.domain

import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp

interface FilterAppsUseCase {
    operator fun invoke(
        query: String,
        apps: List<InstalledApp>,
    ): List<InstalledApp>
}
