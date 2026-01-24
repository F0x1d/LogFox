package com.f0x1d.logfox.feature.apps.picker

data class InstalledApp(val title: String, val packageName: String) {
    val id: String get() = packageName
}
