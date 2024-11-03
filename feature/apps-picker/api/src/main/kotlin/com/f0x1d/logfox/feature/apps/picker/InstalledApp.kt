package com.f0x1d.logfox.feature.apps.picker

import com.f0x1d.logfox.model.Identifiable

data class InstalledApp(
    val title: String,
    val packageName: String,
) : Identifiable {
    override val id: Any get() = packageName
}
