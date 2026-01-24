package com.f0x1d.logfox.feature.preferences.impl.data.ui

import com.fredporciuncula.flow.preferences.Preference

internal interface UISettingsLocalDataSource {
    fun nightTheme(): Preference<Int>
    fun monetEnabled(): Preference<Boolean>
}
