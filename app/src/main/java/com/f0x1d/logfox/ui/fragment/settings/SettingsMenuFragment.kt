package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.R
import com.f0x1d.logfox.ui.fragment.settings.base.BasePreferenceFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsMenuFragment: BasePreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_menu)

        findPreference<Preference>("pref_settings_ui")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsMenuFragmentDirections.actionSettingsMenuFragmentToSettingsUIFragment())
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_service")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsMenuFragmentDirections.actionSettingsMenuFragmentToSettingsServiceFragment())
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_crashes")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsMenuFragmentDirections.actionSettingsMenuFragmentToSettingsCrashesFragment())
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("pref_settings_notifications")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsMenuFragmentDirections.actionSettingsMenuFragmentToSettingsNotificationsFragment())
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("pref_settings_app_version")?.title = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}