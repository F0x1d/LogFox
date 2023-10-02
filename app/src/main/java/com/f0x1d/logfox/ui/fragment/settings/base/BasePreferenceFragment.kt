package com.f0x1d.logfox.ui.fragment.settings.base

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.extensions.isHorizontalOrientation
import dev.chrisbanes.insetter.applyInsetter

abstract class BasePreferenceFragment: PreferenceFragmentCompat() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView.apply {
            clipToPadding = false

            applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = requireContext().isHorizontalOrientation)
                }
            }
        }
    }
}