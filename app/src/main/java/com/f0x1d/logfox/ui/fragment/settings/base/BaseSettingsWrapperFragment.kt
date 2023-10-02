package com.f0x1d.logfox.ui.fragment.settings.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentSettingsBinding
import com.f0x1d.logfox.extensions.isHorizontalOrientation
import com.f0x1d.logfox.extensions.paddingRelative
import com.f0x1d.logfox.ui.fragment.base.BaseFragment
import dev.chrisbanes.insetter.applyInsetter

abstract class BaseSettingsWrapperFragment : BaseFragment<FragmentSettingsBinding>() {

    abstract val wrappedFragment: Fragment
    open val title = R.string.settings
    open val showBackArrow = false

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireContext().also {
            binding.appBarLayout.applyInsetter {
                type(statusBars = true, navigationBars = true, displayCutout = true) {
                    paddingRelative(it, start = !it.isHorizontalOrientation, top = true, end = true)
                }
            }
            binding.container.applyInsetter {
                type(navigationBars = true, displayCutout = true) {
                    paddingRelative(
                        it,
                        start = !it.isHorizontalOrientation,
                        end = true,
                        bottom = true
                    )
                }
            }
        }

        binding.toolbar.setTitle(title)
        if (showBackArrow) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }

        if (savedInstanceState == null) {
            val tag = "wrapped_by_${javaClass.name}"
            val fragment = childFragmentManager.findFragmentByTag(tag)

            childFragmentManager
                .beginTransaction()
                .apply {
                    if (fragment == null)
                        add(R.id.container, wrappedFragment, tag)
                    else
                        show(fragment)
                }
                .commit()
        }
    }
}