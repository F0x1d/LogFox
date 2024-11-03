package com.f0x1d.logfox.feature.settings.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import com.f0x1d.logfox.arch.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.arch.hasNotificationsPermission
import com.f0x1d.logfox.arch.notificationsChannelsAvailable
import com.f0x1d.logfox.feature.settings.R
import com.f0x1d.logfox.feature.settings.presentation.ui.fragment.base.BasePreferenceFragment
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsNotificationsFragment: BasePreferenceFragment() {

    override val title = Strings.notifications
    override val showBackArrow = true

    @SuppressLint("InlinedApi")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_notifications)

        findPreference<Preference>("pref_logging_notification")?.apply {
            isVisible = notificationsChannelsAvailable

            setOnPreferenceClickListener {
                startActivity(
                    Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, LOGGING_STATUS_CHANNEL_ID)
                    }
                )
                return@setOnPreferenceClickListener true
            }
        }

        findPreference<Preference>("pref_per_app_notifications_settings")?.apply {
            isVisible = notificationsChannelsAvailable
        }

        findPreference<Preference>("pref_notifications_permission")?.setOnPreferenceClickListener {
            startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
            )
            return@setOnPreferenceClickListener true
        }
    }

    override fun onStart() {
        super.onStart()

        findPreference<Preference>("pref_notifications_permission")?.isVisible = !requireContext().hasNotificationsPermission()
    }
}
