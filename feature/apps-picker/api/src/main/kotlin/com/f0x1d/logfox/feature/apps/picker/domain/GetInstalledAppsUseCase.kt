package com.f0x1d.logfox.feature.apps.picker.domain

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import kotlinx.collections.immutable.ImmutableList

interface GetInstalledAppsUseCase {
    suspend operator fun invoke(): ImmutableList<InstalledApp>
}
