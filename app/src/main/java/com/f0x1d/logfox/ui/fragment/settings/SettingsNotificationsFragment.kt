package com.f0x1d.logfox.ui.fragment.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.hasNotificationsPermission
import com.f0x1d.logfox.receiver.isAtLeastAndroid13
import com.f0x1d.logfox.ui.fragment.settings.base.BaseSettingsWrapperFragment
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsNotificationsFragment: BaseSettingsWrapperFragment() {

    override val wrappedFragment get() = SettingsNotificationWrappedFragment()
    override val title = R.string.notifications
    override val showBackArrow = true

    @AndroidEntryPoint
    class SettingsNotificationWrappedFragment: PreferenceFragmentCompat() {

        @Inject
        lateinit var appPreferences: AppPreferences

        @SuppressLint("InlinedApi")
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings_notifications)

            findPreference<Preference>("pref_notifications_permission")?.setOnPreferenceClickListener {
                startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                })
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("pref_logging_notification")?.apply {
                isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

                setOnPreferenceClickListener {
                    startActivity(Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                        putExtra(Settings.EXTRA_CHANNEL_ID, LogFoxApp.LOGGING_STATUS_CHANNEL_ID)
                    })
                    return@setOnPreferenceClickListener true
                }
            }

            findPreference<PreferenceCategory>("pref_category_service_start")?.isVisible = isAtLeastAndroid13
            findPreference<SwitchPreferenceCompat>("pref_show_start_service_notification_on_boot")?.apply {
                if (appPreferences.startOnBoot) {
                    isEnabled = false
                    setSummary(R.string.disable_start_on_boot)
                }
            }
        }

        override fun onStart() {
            super.onStart()

            findPreference<Preference>("pref_notifications_permission")?.isVisible = !requireContext().hasNotificationsPermission()
        }
    }
}