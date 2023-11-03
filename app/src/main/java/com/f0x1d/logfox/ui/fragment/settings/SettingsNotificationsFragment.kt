package com.f0x1d.logfox.ui.fragment.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.hasNotificationsPermission
import com.f0x1d.logfox.extensions.notificationsChannelsAvailable
import com.f0x1d.logfox.ui.fragment.settings.base.BasePreferenceFragment
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsNotificationsFragment: BasePreferenceFragment() {

    override val title = R.string.notifications
    override val showBackArrow = true

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
            isVisible = notificationsChannelsAvailable

            setOnPreferenceClickListener {
                startActivity(Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                    putExtra(Settings.EXTRA_CHANNEL_ID, LogFoxApp.LOGGING_STATUS_CHANNEL_ID)
                })
                return@setOnPreferenceClickListener true
            }
        }
    }

    override fun onStart() {
        super.onStart()

        findPreference<Preference>("pref_notifications_permission")?.isVisible = !requireContext().hasNotificationsPermission()
    }
}