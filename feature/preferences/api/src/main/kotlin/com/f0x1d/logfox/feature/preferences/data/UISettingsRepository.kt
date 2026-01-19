package com.f0x1d.logfox.feature.preferences.data

import kotlinx.coroutines.flow.Flow

interface UISettingsRepository {
    var nightTheme: Int
    val nightThemeFlow: Flow<Int>

    var monetEnabled: Boolean
}
