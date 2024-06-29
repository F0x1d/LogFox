package com.f0x1d.logfox.feature.settings.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.f0x1d.logfox.arch.isAtLeastAndroid13
import com.f0x1d.logfox.context.toast
import com.f0x1d.logfox.feature.settings.LoggingServiceDelegate
import com.f0x1d.logfox.feature.settings.R
import com.f0x1d.logfox.feature.settings.fillWithStrings
import com.f0x1d.logfox.feature.settings.ui.fragment.base.BasePreferenceFragment
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.terminals.base.Terminal
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.view.setupAsListPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsServiceFragment: BasePreferenceFragment() {

    override val title = Strings.service
    override val showBackArrow = true

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var terminals: Array<Terminal>

    @Inject
    lateinit var loggingService: LoggingServiceDelegate

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_service)

        findPreference<Preference>("pref_selected_terminal_index")?.apply {
            val filledTerminalSettings = terminals
                .map { it.title }
                .toIntArray()
                .fillWithStrings(requireContext())

            setupAsListPreference(
                setupDialog = { setIcon(Icons.ic_dialog_terminal) },
                items = filledTerminalSettings,
                selected = { appPreferences.selectedTerminalIndex },
                onSelected = {
                    if (appPreferences.selectedTerminalIndex == it) {
                        restartLogging()
                        return@setupAsListPreference
                    }

                    lifecycleScope.launch {
                        val selectedTerminal = terminals[it]

                        if (selectedTerminal.isSupported()) {
                            appPreferences.selectedTerminalIndex = it
                            askAboutNewTerminalRestart()
                        } else
                            requireContext().toast(Strings.terminal_unavailable)
                    }
                }
            )

            appPreferences.selectedTerminalIndexFlow.collectWithLifecycle {
                summary = filledTerminalSettings[it]
            }
        }

        findPreference<SwitchPreferenceCompat>("pref_start_on_boot")?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                val badTerminal = appPreferences.selectedTerminalIndex == com.f0x1d.logfox.terminals.DefaultTerminal.INDEX

                if (isAtLeastAndroid13 && newValue as Boolean && badTerminal) {
                    showAndroid13WarningDialog()
                }
                return@setOnPreferenceChangeListener true
            }
        }

        findPreference<SwitchPreferenceCompat>("pref_show_logs_from_app_launch")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (!(newValue as Boolean)) {
                    restartLogging()
                }

                return@setOnPreferenceChangeListener true
            }
        }
    }

    private fun restartLogging() {
        loggingService.restartLogging()
    }

    private fun askAboutNewTerminalRestart() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(Icons.ic_dialog_terminal)
            .setTitle(Strings.new_terminal_selected)
            .setMessage(Strings.new_terminal_selected_question)
            .setPositiveButton(Strings.yes) { _, _ ->
                restartLogging()
            }
            .setNeutralButton(Strings.no, null)
            .show()
    }

    private fun showAndroid13WarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(Icons.ic_dialog_warning)
            .setTitle(Strings.warning)
            .setMessage(Strings.android13_start_on_boot_warning)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
