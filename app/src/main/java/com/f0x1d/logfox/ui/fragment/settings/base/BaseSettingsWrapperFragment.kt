package com.f0x1d.logfox.ui.fragment.settings.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentSettingsBinding
import com.f0x1d.logfox.ui.fragment.base.BaseFragment

abstract class BaseSettingsWrapperFragment: BaseFragment<FragmentSettingsBinding>() {

    abstract val wrappedFragment: Fragment
    open val title = R.string.settings
    open val showBackArrow = false

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setTitle(title)
        if (showBackArrow) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            binding.toolbar.setOnClickListener { findNavController().popBackStack() }
        }

        if (savedInstanceState == null) {
            childFragmentManager
                .beginTransaction()
                .add(R.id.container, wrappedFragment)
                .commit()
        }
    }
}