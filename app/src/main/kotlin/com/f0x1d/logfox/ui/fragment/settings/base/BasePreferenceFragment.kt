package com.f0x1d.logfox.ui.fragment.settings.base

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.context.isHorizontalOrientation
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import com.google.android.material.appbar.MaterialToolbar
import dev.chrisbanes.insetter.applyInsetter

abstract class BasePreferenceFragment: PreferenceFragmentCompat() {

    open val title = Strings.settings
    open val showBackArrow = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(this@BasePreferenceFragment.title)

            if (showBackArrow) setupBackButtonForNavController()
        }

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
