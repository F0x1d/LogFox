package com.f0x1d.logfox.feature.preferences.impl.data.terminal

import android.content.Context
import androidx.core.content.edit
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TerminalSettingsLocalDataSourceImpl
    @Inject
    constructor(
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
                val terminalType =
                    when (oldIndex) {
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

        override var selectedTerminalType: TerminalType
            get() =
                TerminalType.fromKey(
                    get(KEY_SELECTED_TERMINAL_TYPE, TerminalType.Default.key),
                )
            set(value) = put(KEY_SELECTED_TERMINAL_TYPE, value.key)

        override val selectedTerminalTypeFlow get() =
            flowSharedPreferences
                .getString(
                    key = KEY_SELECTED_TERMINAL_TYPE,
                    defaultValue = TerminalType.Default.key,
                ).asFlow()
                .map(TerminalType::fromKey)

        override var fallbackToDefaultTerminal
            get() = get(KEY_FALLBACK_TO_DEFAULT_TERMINAL, true)
            set(value) = put(KEY_FALLBACK_TO_DEFAULT_TERMINAL, value)

        override fun selectTerminal(type: TerminalType) =
            sharedPreferences.edit(commit = true) {
                putString(KEY_SELECTED_TERMINAL_TYPE, type.key)
            }

        private companion object {
            const val KEY_SELECTED_TERMINAL_TYPE = "pref_selected_terminal_type"
            const val KEY_FALLBACK_TO_DEFAULT_TERMINAL = "pref_fallback_to_default_terminal"
        }
    }
