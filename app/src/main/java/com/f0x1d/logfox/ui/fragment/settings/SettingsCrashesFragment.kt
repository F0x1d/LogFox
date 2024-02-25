package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import com.f0x1d.logfox.R
import com.f0x1d.logfox.ui.fragment.settings.base.BasePreferenceFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsCrashesFragment: BasePreferenceFragment() {

    override val title = R.string.crashes
    override val showBackArrow = true

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_crashes)
    }
}