package com.f0x1d.logfox.feature.preferences.impl.data.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UISettingsLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : BasePreferences(context),
        UISettingsLocalDataSource {
        override var nightTheme
            get() = get(KEY_NIGHT_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            set(value) = put(KEY_NIGHT_THEME, value)

        override val nightThemeFlow get() =
            flowSharedPreferences
                .getInt(
                    key = KEY_NIGHT_THEME,
                    defaultValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                ).asFlow()

        override var monetEnabled
            get() = get(KEY_MONET_ENABLED, true)
            set(value) = put(KEY_MONET_ENABLED, value)

        private companion object {
            const val KEY_NIGHT_THEME = "pref_night_theme"
            const val KEY_MONET_ENABLED = "pref_monet_enabled"
        }
    }
