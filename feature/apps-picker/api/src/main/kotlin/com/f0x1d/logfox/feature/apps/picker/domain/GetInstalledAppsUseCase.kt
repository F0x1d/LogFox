package com.f0x1d.logfox.feature.apps.picker.domain

import com.f0x1d.logfox.feature.apps.picker.InstalledApp

interface GetInstalledAppsUseCase {
    suspend operator fun invoke(): List<InstalledApp>
}
