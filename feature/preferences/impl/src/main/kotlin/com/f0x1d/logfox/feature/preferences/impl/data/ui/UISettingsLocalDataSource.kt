package com.f0x1d.logfox.feature.preferences.impl.data.ui

import kotlinx.coroutines.flow.Flow

internal interface UISettingsLocalDataSource {
    var nightTheme: Int
    val nightThemeFlow: Flow<Int>

    var monetEnabled: Boolean
}
