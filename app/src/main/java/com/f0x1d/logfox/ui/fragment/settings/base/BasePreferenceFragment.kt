package com.f0x1d.logfox.ui.fragment.settings.base

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.isHorizontalOrientation
import com.f0x1d.logfox.extensions.paddingRelative
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import dev.chrisbanes.insetter.applyInsetter

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {

    open val title = R.string.settings
    open val showBackArrow = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        view.findViewById<AppBarLayout>(R.id.app_bar_layout).apply {
            applyInsetter {
                type(statusBars = true, navigationBars = true, displayCutout = true) {
                    paddingRelative(
                        context, start = !context.isHorizontalOrientation, top = true, end = true
                    )
                }
            }
        }
        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(this@BasePreferenceFragment.title)

            if (showBackArrow) {
                setNavigationIcon(R.drawable.ic_arrow_back)
                setNavigationOnClickListener { findNavController().popBackStack() }
            }
        }

        listView.apply {
            clipToPadding = false

            applyInsetter {
                type(navigationBars = true, displayCutout = true) {
                    paddingRelative(
                        context,
                        start = !context.isHorizontalOrientation,
                        bottom = context.isHorizontalOrientation,
                        end = true
                    )
                }
            }
        }
    }
}