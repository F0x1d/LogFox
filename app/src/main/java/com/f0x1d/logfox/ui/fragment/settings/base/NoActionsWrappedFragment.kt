package com.f0x1d.logfox.ui.fragment.settings.base

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoActionsWrappedFragment(private val preferenceResource: Int): PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(preferenceResource)
    }
}