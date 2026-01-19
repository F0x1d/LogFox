package com.f0x1d.logfox.feature.preferences.presentation.notifications.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import com.f0x1d.logfox.core.context.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.core.context.hasNotificationsPermission
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.presentation.view.setupBackButtonForNavController
import com.f0x1d.logfox.core.tea.BaseStorePreferenceFragment
import com.f0x1d.logfox.feature.preferences.presentation.R
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsCommand
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsState
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsViewModel
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class PreferencesNotificationsFragment : BaseStorePreferenceFragment<
    PreferencesNotificationsState,
    PreferencesNotificationsCommand,
    PreferencesNotificationsSideEffect,
    PreferencesNotificationsViewModel,
>() {

    override val viewModel by viewModels<PreferencesNotificationsViewModel>()

    @SuppressLint("InlinedApi")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_notifications)

        findPreference<Preference>("pref_logging_notification")?.setOnPreferenceClickListener {
            send(PreferencesNotificationsCommand.OpenLoggingNotificationSettings)
            true
        }

        findPreference<Preference>("pref_notifications_permission")?.setOnPreferenceClickListener {
            send(PreferencesNotificationsCommand.OpenNotificationsPermissionSettings)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(Strings.notifications)
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

    override fun onStart() {
        super.onStart()
        send(
            PreferencesNotificationsCommand.PermissionChecked(
                hasPermission = requireContext().hasNotificationsPermission()
            )
        )
    }

    override fun render(state: PreferencesNotificationsState) {
        findPreference<Preference>("pref_logging_notification")?.apply {
            isVisible = state.notificationsChannelsAvailable
        }
        findPreference<Preference>("pref_per_app_notifications_settings")?.apply {
            isVisible = state.notificationsChannelsAvailable
        }
        findPreference<Preference>("pref_notifications_permission")?.apply {
            isVisible = !state.hasNotificationsPermission
        }
    }

    @SuppressLint("InlinedApi")
    override fun handleSideEffect(sideEffect: PreferencesNotificationsSideEffect) {
        when (sideEffect) {
            is PreferencesNotificationsSideEffect.OpenLoggingChannelSettings -> {
                startActivity(
                    Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, LOGGING_STATUS_CHANNEL_ID)
                    }
                )
            }

            is PreferencesNotificationsSideEffect.OpenAppNotificationSettings -> {
                startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    }
                )
            }
        }
    }
}
