package com.f0x1d.logfox.ui.fragment.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.isOmnibinInstalled
import com.f0x1d.logfox.ui.fragment.settings.base.BaseSettingsWrapperFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsMenuFragment: BaseSettingsWrapperFragment() {

    override val wrappedFragment get() = SettingsMenuWrappedFragment()

    @AndroidEntryPoint
    class SettingsMenuWrappedFragment: PreferenceFragmentCompat() {

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

            findPreference<Preference>("pref_omnibin_integration")?.setOnPreferenceClickListener {
                startActivity(if (requireContext().isOmnibinInstalled())
                    requireContext().packageManager.getLaunchIntentForPackage("com.f0x1d.dogbin")
                else
                    Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.f0x1d.dogbin"))
                )
                return@setOnPreferenceClickListener true
            }
        }
    }
}