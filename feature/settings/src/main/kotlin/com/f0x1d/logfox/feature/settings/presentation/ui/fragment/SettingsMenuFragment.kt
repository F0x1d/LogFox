package com.f0x1d.logfox.feature.settings.presentation.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.f0x1d.logfox.arch.logs.timberLogFile
import com.f0x1d.logfox.arch.shareFileIntent
import com.f0x1d.logfox.feature.settings.BuildConfig
import com.f0x1d.logfox.feature.settings.R
import com.f0x1d.logfox.feature.settings.presentation.ui.fragment.base.BasePreferenceFragment
import com.f0x1d.logfox.navigation.Directions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsMenuFragment: BasePreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_menu)

        findPreference<Preference>("pref_settings_ui")?.setOnPreferenceClickListener {
            findNavController().navigate(Directions.action_settingsMenuFragment_to_settingsUIFragment)
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_service")?.setOnPreferenceClickListener {
            findNavController().navigate(Directions.action_settingsMenuFragment_to_settingsServiceFragment)
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_crashes")?.setOnPreferenceClickListener {
            findNavController().navigate(Directions.action_settingsMenuFragment_to_settingsCrashesFragment)
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_notifications")?.setOnPreferenceClickListener {
            findNavController().navigate(Directions.action_settingsMenuFragment_to_settingsNotificationsFragment)
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("pref_settings_links")?.setOnPreferenceClickListener {
            findNavController().navigate(Directions.action_settingsMenuFragment_to_settingsLinksFragment)
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_app_version")?.apply {
            val packageManager = requireContext().packageManager
            val packageInfo = packageManager.getPackageInfo(requireContext().packageName, 0)

            title = "${packageInfo.versionName} (${packageInfo.versionCode})"
        }
        findPreference<Preference>("pref_settings_share_logs")?.apply {
            isVisible = BuildConfig.DEBUG

            setOnPreferenceClickListener {
                requireContext().shareFileIntent(requireContext().timberLogFile)
                return@setOnPreferenceClickListener true
            }
        }
    }
}
