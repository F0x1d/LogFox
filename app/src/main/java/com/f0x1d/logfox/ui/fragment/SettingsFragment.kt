package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_settings)
    }
}