package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.databinding.FragmentExtendedCopyBinding
import com.f0x1d.logfox.extensions.views.setupBackButtonForNavController
import com.f0x1d.logfox.ui.fragment.base.BaseFragment
import dev.chrisbanes.insetter.applyInsetter

class ExtendedCopyFragment: BaseFragment<FragmentExtendedCopyBinding>() {

    private val navArgs by navArgs<ExtendedCopyFragmentArgs>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentExtendedCopyBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
        binding.toolbar.setupBackButtonForNavController()

        binding.logText.text = navArgs.content
    }
}