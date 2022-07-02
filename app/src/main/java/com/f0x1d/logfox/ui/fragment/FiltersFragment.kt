package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.FiltersAdapter
import com.f0x1d.logfox.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.f0x1d.logfox.viewmodel.filters.FiltersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersFragment: BaseViewModelFragment<FiltersViewModel, FragmentFiltersBinding>() {

    override val viewModel by viewModels<FiltersViewModel>()
    private val logsViewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    private val adapter = FiltersAdapter({
        findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToFilterBottomSheet(it.id))
    }, {
        viewModel.delete(it)
    })

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentFiltersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.inflateMenu(R.menu.filters_menu)
        binding.toolbar.menu.apply {
            findItem(R.id.create_item).setOnMenuItemClickListener {
                findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToFilterBottomSheet())
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.clear_item).setOnMenuItemClickListener {
                viewModel.clearAll()
                return@setOnMenuItemClickListener true
            }
        }

        binding.filtersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.filtersRecycler.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.elements = it ?: return@observe
            logsViewModel.recollect()
        }
    }
}