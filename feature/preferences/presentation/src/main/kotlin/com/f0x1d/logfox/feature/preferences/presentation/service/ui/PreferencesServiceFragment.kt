package com.f0x1d.logfox.feature.preferences.presentation.service.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.core.tea.BaseStorePreferenceFragment
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.preference.setupAsListPreference
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.feature.preferences.presentation.R
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceCommand
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceState
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceViewModel
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class PreferencesServiceFragment :
    BaseStorePreferenceFragment<
        PreferencesServiceState,
        PreferencesServiceState,
        PreferencesServiceCommand,
        PreferencesServiceSideEffect,
        PreferencesServiceViewModel,
        >() {

    override val viewModel by viewModels<PreferencesServiceViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_service)

        findPreference<SwitchPreferenceCompat>("pref_start_on_boot")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                send(PreferencesServiceCommand.StartOnBootChanged(newValue as Boolean))
                true
            }
        }

        findPreference<SwitchPreferenceCompat>("pref_show_logs_from_app_launch")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                send(PreferencesServiceCommand.ShowLogsFromAppLaunchChanged(newValue as Boolean))
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(Strings.service)
            setupBackButtonForNavController()
        }

        listView.apply {
            clipToPadding = false
            applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = requireContext().isHorizontalOrientation)
                }
            }
        }
    }

    override fun render(state: PreferencesServiceState) {
        if (state.terminalNames.isEmpty()) return

        findPreference<Preference>("pref_selected_terminal_index")?.apply {
            val terminalNamesArray = state.terminalNames.toTypedArray()
            val selectedIndex = TerminalType.entries.indexOf(state.selectedTerminalType)

            setupAsListPreference(
                setupDialog = { setIcon(Icons.ic_dialog_terminal) },
                items = terminalNamesArray,
                selected = { selectedIndex },
                onSelected = { index ->
                    val type = TerminalType.entries[index]
                    send(PreferencesServiceCommand.TerminalSelected(type))
                },
            )

            summary = terminalNamesArray.getOrNull(selectedIndex) ?: ""
        }
    }

    override fun handleSideEffect(sideEffect: PreferencesServiceSideEffect) {
        when (sideEffect) {
            is PreferencesServiceSideEffect.ShowTerminalRestartDialog -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(Icons.ic_dialog_terminal)
                    .setTitle(Strings.new_terminal_selected)
                    .setMessage(Strings.new_terminal_selected_question)
                    .setPositiveButton(Strings.yes) { _, _ ->
                        send(PreferencesServiceCommand.ConfirmRestartLogging)
                    }
                    .setNeutralButton(Strings.no, null)
                    .show()
            }

            is PreferencesServiceSideEffect.ShowTerminalUnavailableToast -> {
                requireContext().toast(Strings.terminal_unavailable)
            }

            is PreferencesServiceSideEffect.ShowAndroid13WarningDialog -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(Icons.ic_dialog_warning)
                    .setTitle(Strings.warning)
                    .setMessage(Strings.android13_start_on_boot_warning)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }
}
