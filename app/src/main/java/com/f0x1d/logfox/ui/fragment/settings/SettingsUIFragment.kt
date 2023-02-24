package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.ui.fragment.settings.base.BaseSettingsWrapperFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsUIFragment: BaseSettingsWrapperFragment() {

    override val wrappedFragment get() = SettingsUIWrappedFragment()
    override val title = R.string.ui
    override val showBackArrow = true

    @AndroidEntryPoint
    class SettingsUIWrappedFragment: PreferenceFragmentCompat() {

        @Inject
        lateinit var appPreferences: AppPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings_ui)

            findPreference<Preference>("pref_night_theme")?.apply {
                val filledThemeSettings = intArrayOf(R.string.follow_system, R.string.light, R.string.dark).fillWithStrings(requireContext())

                setupAsListPreference(
                    {
                        setIcon(R.drawable.ic_dialog_theme)
                    },
                    filledThemeSettings,
                    appPreferences.nightTheme
                ) {
                    appPreferences.nightTheme = it
                    requireActivity().applyTheme(it, true)
                }

                observeAndUpdateSummaryForList(appPreferences, this@SettingsUIWrappedFragment, 0, filledThemeSettings)
            }

            findPreference<Preference>("pref_logs_format")?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.logs_format)
                    .setIcon(R.drawable.ic_dialog_list)
                    .setMultiChoiceItems(
                        intArrayOf(R.string.time, R.string.pid, R.string.tid, R.string.tag, R.string.content).fillWithStrings(requireContext()),
                        appPreferences.showLogValues
                    ) { dialog, which, checked ->
                        when (which) {
                            0 -> appPreferences.showLogTime = checked
                            1 -> appPreferences.showLogPid = checked
                            2 -> appPreferences.showLogTid = checked
                            3 -> appPreferences.showLogTag = checked
                            4 -> appPreferences.showLogContent = checked
                        }
                    }
                    .setPositiveButton(android.R.string.cancel, null)
                    .show()
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("pref_logs_update_interval")?.apply {
                setupAsEditTextPreference({
                    it.textLayout.setHint(R.string.in_ms)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                }, {
                   setIcon(R.drawable.ic_dialog_timer)
                }, {
                    appPreferences.logsUpdateInterval.toString()
                }, {
                    requireContext().catchingNotNumber {
                        appPreferences.logsUpdateInterval = it?.toLong() ?: AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
                    }
                })

                observeAndUpdateSummary(appPreferences, this@SettingsUIWrappedFragment, AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT)
            }

            findPreference<Preference>("pref_logs_text_size")?.apply {
                setupAsEditTextPreference({
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                }, {
                   setIcon(R.drawable.ic_dialog_text_fields)
                }, {
                    appPreferences.logsTextSize.toString()
                }, {
                    requireContext().catchingNotNumber {
                        appPreferences.logsTextSize = it?.toInt() ?: AppPreferences.LOGS_TEXT_SIZE_DEFAULT
                    }
                })

                observeAndUpdateSummary(appPreferences, this@SettingsUIWrappedFragment, AppPreferences.LOGS_TEXT_SIZE_DEFAULT)
            }
        }
    }
}