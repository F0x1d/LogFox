package com.f0x1d.logfox.feature.preferences.impl.data.terminal

import android.content.Context
import androidx.core.content.edit
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TerminalSettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : BasePreferences(context),
    TerminalSettingsLocalDataSource {

    init {
        migrateTerminalIndexToType()
    }

    private fun migrateTerminalIndexToType() {
        val oldKey = "pref_selected_terminal_index"
        val newKey = KEY_SELECTED_TERMINAL_TYPE

        if (sharedPreferences.contains(oldKey) && !sharedPreferences.contains(newKey)) {
            val oldIndex = sharedPreferences.getInt(oldKey, 0)
            val terminalType = when (oldIndex) {
                0 -> TerminalType.Default
                1 -> TerminalType.Root
                2 -> TerminalType.Shizuku
                else -> TerminalType.Default
            }
            sharedPreferences.edit {
                putString(newKey, terminalType.key)
                remove(oldKey)
            }
        }
    }

    override fun selectedTerminalTypeKey(): Preference<String> = stringPreference(
        key = KEY_SELECTED_TERMINAL_TYPE,
        defaultValue = TerminalType.Default.key,
    )

    override fun fallbackToDefaultTerminal(): Preference<Boolean> = booleanPreference(
        key = KEY_FALLBACK_TO_DEFAULT_TERMINAL,
        defaultValue = true,
    )

    private companion object {
        const val KEY_SELECTED_TERMINAL_TYPE = "pref_selected_terminal_type"
        const val KEY_FALLBACK_TO_DEFAULT_TERMINAL = "pref_fallback_to_default_terminal"
    }
}
