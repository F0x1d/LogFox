package com.f0x1d.logfox.feature.preferences.presentation.menu

internal data class PreferencesMenuState(
    val versionName: String = "",
    val versionCode: Int = 0,
    val isDebug: Boolean = false,
)
