package com.f0x1d.logfox.feature.settings.presentation.ui.fragment

import android.os.Bundle
import com.f0x1d.logfox.feature.settings.R
import com.f0x1d.logfox.feature.settings.presentation.ui.fragment.base.BasePreferenceFragment
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsLinksFragment : BasePreferenceFragment() {

    override val title = Strings.links
    override val showBackArrow = true

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_links)
    }
}
