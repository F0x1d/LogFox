package com.f0x1d.logfox.feature.apps.picker.impl.domain

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import com.f0x1d.logfox.feature.apps.picker.domain.GetInstalledAppsUseCase
import com.f0x1d.logfox.feature.apps.picker.impl.data.InstalledAppsDataSource
import javax.inject.Inject

internal class GetInstalledAppsUseCaseImpl @Inject constructor(
    private val installedAppsDataSource: InstalledAppsDataSource,
) : GetInstalledAppsUseCase {

    override suspend fun invoke(): List<InstalledApp> = installedAppsDataSource.getInstalledApps()
}
