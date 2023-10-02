package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.Preference
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.applyTheme
import com.f0x1d.logfox.extensions.catchingNotNumber
import com.f0x1d.logfox.extensions.observeAndUpdateSummary
import com.f0x1d.logfox.extensions.observeAndUpdateSummaryForList
import com.f0x1d.logfox.extensions.setupAsEditTextPreference
import com.f0x1d.logfox.extensions.setupAsListPreference
import com.f0x1d.logfox.ui.fragment.settings.base.BasePreferenceFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsUIFragment: BasePreferenceFragment() {

    override val title = R.string.ui
    override val showBackArrow = true

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
                { appPreferences.nightTheme }
            ) {
                appPreferences.nightTheme = it
                requireActivity().applyTheme(it, true)
            }

            observeAndUpdateSummaryForList(appPreferences, this@SettingsUIFragment, 0, filledThemeSettings)
        }

        findPreference<Preference>("pref_logs_format")?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logs_format)
                .setIcon(R.drawable.ic_dialog_list)
                .setMultiChoiceItems(
                    intArrayOf(
                        R.string.date,
                        R.string.time,
                        R.string.uid,
                        R.string.pid,
                        R.string.tid,
                        R.string.package_name,
                        R.string.tag,
                        R.string.content
                    ).fillWithStrings(requireContext()),
                    appPreferences.showLogValues.asArray
                ) { dialog, which, checked ->
                    when (which) {
                        0 -> appPreferences.showLogDate = checked
                        1 -> appPreferences.showLogTime = checked
                        2 -> appPreferences.showLogUid = checked
                        3 -> appPreferences.showLogPid = checked
                        4 -> appPreferences.showLogTid = checked
                        5 -> appPreferences.showLogPackage = checked
                        6 -> appPreferences.showLogTag = checked
                        7 -> appPreferences.showLogContent = checked
                    }
                }
                .setPositiveButton(R.string.close, null)
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

            observeAndUpdateSummary(appPreferences, this@SettingsUIFragment, AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT)
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

            observeAndUpdateSummary(appPreferences, this@SettingsUIFragment, AppPreferences.LOGS_TEXT_SIZE_DEFAULT)
        }

        findPreference<Preference>("pref_logs_display_limit")?.apply {
            setupAsEditTextPreference({
                it.textLayout.setHint(R.string.lines)
                it.text.inputType = InputType.TYPE_CLASS_NUMBER
            }, {
                setIcon(R.drawable.ic_dialog_eye)
            }, {
                appPreferences.logsDisplayLimit.toString()
            }, {
                requireContext().catchingNotNumber {
                    appPreferences.logsDisplayLimit = it?.toInt() ?: AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT
                }
            })

            observeAndUpdateSummary(appPreferences, this@SettingsUIFragment, AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT)
        }
    }
}