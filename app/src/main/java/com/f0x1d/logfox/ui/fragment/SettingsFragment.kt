package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentSettingsBinding
import com.f0x1d.logfox.extensions.catchingNotNumber
import com.f0x1d.logfox.extensions.isOmnibinInstalled
import com.f0x1d.logfox.extensions.setupAsEditTextPreference
import com.f0x1d.logfox.ui.fragment.base.BaseFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: BaseFragment<FragmentSettingsBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager
                .beginTransaction()
                .add(R.id.container, SettingsWrapperFragment())
                .commit()
        }
    }

    @AndroidEntryPoint
    class SettingsWrapperFragment: PreferenceFragmentCompat() {

        @Inject
        lateinit var appPreferences: AppPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.app_settings)

            findPreference<Preference>("pref_logs_format")?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.logs_format)
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
                    .setNeutralButton(android.R.string.cancel, null)
                    .show()
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("pref_logs_update_interval")?.apply {
                setupAsEditTextPreference({
                    it.textLayout.setHint(R.string.in_ms)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                }, {
                    appPreferences.logsUpdateInterval.toString()
                }, {
                    requireContext().catchingNotNumber {
                        appPreferences.logsUpdateInterval = it?.toLong() ?: AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
                    }
                })

                observeAndUpdateSummary(AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT)
            }

            findPreference<Preference>("pref_logs_text_size")?.apply {
                setupAsEditTextPreference({
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                }, {
                    appPreferences.logsTextSize.toString()
                }, {
                    requireContext().catchingNotNumber {
                        appPreferences.logsTextSize = it?.toInt() ?: AppPreferences.LOGS_TEXT_SIZE_DEFAULT
                    }
                })

                observeAndUpdateSummary(AppPreferences.LOGS_TEXT_SIZE_DEFAULT)
            }
        }

        override fun onStart() {
            super.onStart()

            findPreference<Preference>("pref_omnibin_integration")?.isEnabled = !requireContext().isOmnibinInstalled()
        }

        private inline fun <reified T> Preference.observeAndUpdateSummary(defValue: T) {
            appPreferences.asLiveData(key, defValue).observe(this@SettingsWrapperFragment) {
                summary = it.toString()
            }
        }
    }
}