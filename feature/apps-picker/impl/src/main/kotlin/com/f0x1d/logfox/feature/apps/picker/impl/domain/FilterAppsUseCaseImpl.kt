package com.f0x1d.logfox.feature.apps.picker.impl.domain

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import com.f0x1d.logfox.feature.apps.picker.domain.FilterAppsUseCase
import javax.inject.Inject

internal class FilterAppsUseCaseImpl @Inject constructor() : FilterAppsUseCase {

    override fun invoke(
        query: String,
        apps: List<InstalledApp>,
    ): List<InstalledApp> = apps.filter { app ->
        app.title.contains(query, ignoreCase = true) ||
            app.packageName.contains(query, ignoreCase = true)
    }
}
