package com.f0x1d.logfox.feature.apps.picker.domain

import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import kotlinx.collections.immutable.ImmutableList

interface FilterAppsUseCase {
    operator fun invoke(query: String, apps: ImmutableList<InstalledApp>): ImmutableList<InstalledApp>
}
