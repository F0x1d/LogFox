package com.f0x1d.logfox.feature.apps.picker.api.domain

import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp

interface GetInstalledAppsUseCase {
    suspend operator fun invoke(): List<InstalledApp>
}
