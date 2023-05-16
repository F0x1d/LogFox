package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.haveRoot
import com.f0x1d.logfox.receiver.isAtLeastAndroid13
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.ui.fragment.settings.base.BaseSettingsWrapperFragment
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsServiceFragment: BaseSettingsWrapperFragment() {

    override val wrappedFragment get() = SettingsServiceWrappedFragment()
    override val title = R.string.service
    override val showBackArrow = true

    @AndroidEntryPoint
    class SettingsServiceWrappedFragment: PreferenceFragmentCompat() {

        @Inject
        lateinit var loggingRepository: LoggingRepository

        @Inject
        lateinit var appPreferences: AppPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings_service)

            findPreference<SwitchPreferenceCompat>("pref_start_on_boot")?.apply {
                if (appPreferences.showStartServiceNotificationOnBoot) {
                    isEnabled = false
                    setSummary(R.string.disable_reminder_notification_on_boot)
                }

                setOnPreferenceChangeListener { preference, newValue ->
                    if (isAtLeastAndroid13 && newValue as Boolean && !loggingRepository.rootStateFlow.value.haveRoot) {
                        showAndroid13WarningDialog()
                    }
                    return@setOnPreferenceChangeListener true
                }
            }
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
}