package com.f0x1d.logfox.feature.preferences.impl.data.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UISettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : BasePreferences(context), UISettingsLocalDataSource {

    override fun nightTheme(): Preference<Int> = intPreference(
        key = KEY_NIGHT_THEME,
        defaultValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
    )

    override fun monetEnabled(): Preference<Boolean> = booleanPreference(
        key = KEY_MONET_ENABLED,
        defaultValue = true,
    )

    private companion object {
        const val KEY_NIGHT_THEME = "pref_night_theme"
        const val KEY_MONET_ENABLED = "pref_monet_enabled"
    }
}
