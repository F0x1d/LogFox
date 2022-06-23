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
import com.f0x1d.logfox.extensions.setupAsEditTextPreference
import com.f0x1d.logfox.ui.fragment.base.BaseFragment
import com.f0x1d.logfox.utils.preferences.AppPreferences
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

            findPreference<Preference>("pref_logs_update_interval")?.apply {
                setupAsEditTextPreference({
                    it.textLayout.setHint(R.string.in_ms)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                }, {
                    appPreferences.logsUpdateInterval.toString()
                }, {
                    requireContext().catchingNotNumber {
                        appPreferences.logsUpdateInterval = it?.toLong() ?: 150L
                    }
                })

                observeAndUpdateSummary(150L)
            }

            findPreference<Preference>("pref_logs_text_size")?.apply {
                setupAsEditTextPreference({
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                }, {
                    appPreferences.logsTextSize.toString()
                }, {
                    requireContext().catchingNotNumber {
                        appPreferences.logsTextSize = it?.toInt() ?: 14
                    }
                })

                observeAndUpdateSummary(14)
            }
        }

        private inline fun <reified T> Preference.observeAndUpdateSummary(defValue: T) {
            appPreferences.asLiveData(key, defValue).observe(this@SettingsWrapperFragment) {
                summary = it.toString()
            }
        }
    }
}