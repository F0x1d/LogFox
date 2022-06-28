package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.databinding.FragmentExtendedCopyBinding
import com.f0x1d.logfox.ui.fragment.base.BaseFragment

class ExtendedCopyFragment: BaseFragment<FragmentExtendedCopyBinding>() {

    private val navArgs by navArgs<ExtendedCopyFragmentArgs>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentExtendedCopyBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.text.text = navArgs.content
    }
}