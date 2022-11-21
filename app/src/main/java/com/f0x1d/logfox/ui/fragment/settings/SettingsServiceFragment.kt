package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.ui.fragment.settings.base.BaseSettingsWrapperFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsServiceFragment: BaseSettingsWrapperFragment() {

    override val wrappedFragment get() = SettingsServiceWrappedFragment()
    override val title = R.string.service
    override val showBackArrow = true

    @AndroidEntryPoint
    class SettingsServiceWrappedFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings_service)
        }
    }
}