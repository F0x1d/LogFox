package com.f0x1d.logfox.feature.settings.ui.fragment

import android.os.Bundle
import android.text.InputType
import androidx.preference.Preference
import com.f0x1d.logfox.context.catchingNotNumber
import com.f0x1d.logfox.feature.settings.R
import com.f0x1d.logfox.feature.settings.fillWithStrings
import com.f0x1d.logfox.feature.settings.ui.fragment.base.BasePreferenceFragment
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.view.observeAndUpdateSummary
import com.f0x1d.logfox.ui.view.setupAsEditTextPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsUIFragment: BasePreferenceFragment() {

    override val title = Strings.ui
    override val showBackArrow = true

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_ui)

        findPreference<Preference>("pref_date_format")?.apply {
            setupAsEditTextPreference(
                setupViews = { it.textLayout.setHint(Strings.date_format) },
                setupDialog = { setIcon(Icons.ic_dialog_date_format) },
                get = { appPreferences.dateFormat },
                save = {
                    appPreferences.dateFormat = it?.trim() ?: AppPreferences.DATE_FORMAT_DEFAULT
                }
            )

            observeAndUpdateSummary(
                observer = this@SettingsUIFragment,
                defValue = AppPreferences.DATE_FORMAT_DEFAULT
            )
        }

        findPreference<Preference>("pref_time_format")?.apply {
            setupAsEditTextPreference(
                setupViews = { it.textLayout.setHint(Strings.time_format) },
                setupDialog = { setIcon(Icons.ic_dialog_time_format) },
                get = { appPreferences.timeFormat },
                save = {
                    appPreferences.timeFormat = it?.trim() ?: AppPreferences.TIME_FORMAT_DEFAULT
                }
            )

            observeAndUpdateSummary(
                observer = this@SettingsUIFragment,
                defValue = AppPreferences.TIME_FORMAT_DEFAULT
            )
        }

        findPreference<Preference>("pref_logs_format")?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(Strings.logs_format)
                .setIcon(Icons.ic_dialog_list)
                .setMultiChoiceItems(
                    intArrayOf(
                        Strings.date,
                        Strings.time,
                        Strings.uid,
                        Strings.pid,
                        Strings.tid,
                        Strings.package_name,
                        Strings.tag,
                        Strings.content
                    ).fillWithStrings(requireContext()),
                    appPreferences.showLogValues.asArray
                ) { _, which, checked ->
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
                .setPositiveButton(Strings.close, null)
                .show()
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("pref_logs_update_interval")?.apply {
            setupAsEditTextPreference(
                setupViews = {
                    it.textLayout.setHint(Strings.in_ms)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                },
                setupDialog = { setIcon(Icons.ic_dialog_timer) },
                get = { appPreferences.logsUpdateInterval.toString() },
                save = {
                    requireContext().catchingNotNumber {
                        appPreferences.logsUpdateInterval = it?.toLong() ?: AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
                    }
                }
            )

            observeAndUpdateSummary(
                observer = this@SettingsUIFragment,
                defValue = AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
            )
        }

        findPreference<Preference>("pref_logs_text_size")?.apply {
            setupAsEditTextPreference(
                setupViews = { it.text.inputType = InputType.TYPE_CLASS_NUMBER },
                setupDialog = { setIcon(Icons.ic_dialog_text_fields) },
                get = { appPreferences.logsTextSize.toString() },
                save = {
                    requireContext().catchingNotNumber {
                        appPreferences.logsTextSize = it?.toInt() ?: AppPreferences.LOGS_TEXT_SIZE_DEFAULT
                    }
                }
            )

            observeAndUpdateSummary(
                observer = this@SettingsUIFragment,
                defValue = AppPreferences.LOGS_TEXT_SIZE_DEFAULT
            )
        }

        findPreference<Preference>("pref_logs_display_limit")?.apply {
            setupAsEditTextPreference(
                setupViews = {
                    it.textLayout.setHint(Strings.lines)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                },
                setupDialog = { setIcon(Icons.ic_dialog_eye) },
                get = { appPreferences.logsDisplayLimit.toString() },
                save = {
                    requireContext().catchingNotNumber {
                        appPreferences.logsDisplayLimit = it
                            ?.toInt()
                            ?.coerceAtLeast(0)
                            ?: AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT
                    }
                }
            )

            observeAndUpdateSummary(
                observer = this@SettingsUIFragment,
                defValue = AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT
            )
        }
    }
}
