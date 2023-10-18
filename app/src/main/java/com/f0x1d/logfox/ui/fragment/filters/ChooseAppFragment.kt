package com.f0x1d.logfox.ui.fragment.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.AppsAdapter
import com.f0x1d.logfox.databinding.FragmentChooseAppBinding
import com.f0x1d.logfox.extensions.dpToPx
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.filters.ChooseAppViewModel
import com.f0x1d.logfox.viewmodel.filters.EditFilterViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class ChooseAppFragment: BaseViewModelFragment<ChooseAppViewModel, FragmentChooseAppBinding>() {

    override val viewModel by viewModels<ChooseAppViewModel>()

    private val editFilterViewModel by hiltNavGraphViewModels<EditFilterViewModel>(R.id.editFilterFragment)

    private val adapter = AppsAdapter {
        editFilterViewModel.selectApp(it)
        findNavController().popBackStack()
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChooseAppBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appsRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.toolbar.setupBackButtonForNavController()

        binding.appsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.appsRecycler.addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            dividerInsetStart = 80.dpToPx.toInt()
            dividerInsetEnd = 10.dpToPx.toInt()
            isLastItemDecorated = false
        })
        binding.appsRecycler.adapter = adapter

        viewModel.apps.asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}