package com.f0x1d.logfox.feature.preferences.presentation.menu.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.context.shareFileIntent
import com.f0x1d.logfox.core.tea.BaseStorePreferenceFragment
import com.f0x1d.logfox.feature.preferences.presentation.R
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuCommand
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuState
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuViewModel
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.navigation.Directions
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class PreferencesMenuFragment :
    BaseStorePreferenceFragment<
        PreferencesMenuState,
        PreferencesMenuCommand,
        PreferencesMenuSideEffect,
        PreferencesMenuViewModel,
        >() {

    override val viewModel by viewModels<PreferencesMenuViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_menu)

        findPreference<Preference>("pref_settings_ui")?.setOnPreferenceClickListener {
            send(PreferencesMenuCommand.UISettingsClicked)
            true
        }
        findPreference<Preference>("pref_settings_service")?.setOnPreferenceClickListener {
            send(PreferencesMenuCommand.ServiceSettingsClicked)
            true
        }
        findPreference<Preference>("pref_settings_crashes")?.setOnPreferenceClickListener {
            send(PreferencesMenuCommand.CrashesSettingsClicked)
            true
        }
        findPreference<Preference>("pref_settings_notifications")?.setOnPreferenceClickListener {
            send(PreferencesMenuCommand.NotificationsSettingsClicked)
            true
        }
        findPreference<Preference>("pref_settings_links")?.setOnPreferenceClickListener {
            send(PreferencesMenuCommand.LinksClicked)
            true
        }
        findPreference<Preference>("pref_settings_share_logs")?.setOnPreferenceClickListener {
            send(PreferencesMenuCommand.ShareLogsClicked)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar)?.apply {
            setTitle(Strings.settings)
        }

        listView?.apply {
            clipToPadding = false
            applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = requireContext().isHorizontalOrientation)
                }
            }
        }
    }

    override fun render(state: PreferencesMenuState) {
        findPreference<Preference>("pref_settings_app_version")?.apply {
            title = "${state.versionName} (${state.versionCode})"
        }
        findPreference<Preference>("pref_settings_share_logs")?.apply {
            isVisible = state.isDebug
        }
    }

    override fun handleSideEffect(sideEffect: PreferencesMenuSideEffect) {
        when (sideEffect) {
            is PreferencesMenuSideEffect.NavigateToUISettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsUIFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToServiceSettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsServiceFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToCrashesSettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsCrashesFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToNotificationsSettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsNotificationsFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToLinks -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsLinksFragment,
                )
            }

            is PreferencesMenuSideEffect.ShareLogs -> {
                requireContext().shareFileIntent(sideEffect.file)
            }
        }
    }
}
