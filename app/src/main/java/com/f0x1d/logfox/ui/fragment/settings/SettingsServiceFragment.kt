package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.toast
import com.f0x1d.logfox.extensions.fillWithStrings
import com.f0x1d.logfox.extensions.isAtLeastAndroid13
import com.f0x1d.logfox.extensions.views.widgets.observeAndUpdateSummaryForList
import com.f0x1d.logfox.extensions.views.widgets.setupAsListPreference
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.ui.fragment.settings.base.BasePreferenceFragment
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.utils.terminal.DefaultTerminal
import com.f0x1d.logfox.utils.terminal.base.Terminal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsServiceFragment: BasePreferenceFragment() {

    override val title = R.string.service
    override val showBackArrow = true

    @Inject
    lateinit var loggingRepository: LoggingRepository

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var terminals: Array<Terminal>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_service)

        findPreference<Preference>("pref_selected_terminal_index")?.apply {
            val filledTerminalSettings = terminals
                .map { it.title }
                .toIntArray()
                .fillWithStrings(requireContext())

            setupAsListPreference(
                setupDialog = { setIcon(R.drawable.ic_dialog_terminal) },
                items = filledTerminalSettings,
                selected = { appPreferences.selectedTerminalIndex },
                onSelected = {
                    if (appPreferences.selectedTerminalIndex == it) {
                        loggingRepository.restartLogging()
                        return@setupAsListPreference
                    }

                    lifecycleScope.launch {
                        val selectedTerminal = terminals[it]
                        if (selectedTerminal.isSupported()) {
                            appPreferences.selectedTerminalIndex = it
                            askAboutNewTerminalRestart()
                        } else
                            requireContext().toast(R.string.terminal_unavailable)
                    }
                }
            )

            observeAndUpdateSummaryForList(
                observer = this@SettingsServiceFragment,
                defValue = 0,
                items = filledTerminalSettings
            )
        }

        findPreference<SwitchPreferenceCompat>("pref_start_on_boot")?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                val badTerminal = appPreferences.selectedTerminalIndex == DefaultTerminal.INDEX

                if (isAtLeastAndroid13 && newValue as Boolean && badTerminal) {
                    showAndroid13WarningDialog()
                }
                return@setOnPreferenceChangeListener true
            }
        }

        findPreference<SwitchPreferenceCompat>("pref_show_logs_from_app_launch")?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                if (!(newValue as Boolean)) {
                    loggingRepository.restartLogging(updateTerminal = false)
                }

                return@setOnPreferenceChangeListener true
            }
        }
    }

    private fun askAboutNewTerminalRestart() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_dialog_terminal)
            .setTitle(R.string.new_terminal_selected)
            .setMessage(R.string.new_terminal_selected_question)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                loggingRepository.restartLogging()
            }
            .setNeutralButton(R.string.no, null)
            .show()
    }

    private fun showAndroid13WarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_dialog_warning)
            .setTitle(R.string.warning)
            .setMessage(R.string.android13_start_on_boot_warning)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}