package com.f0x1d.logfox.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.FiltersAdapter
import com.f0x1d.logfox.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.extensions.setClickListenerOn
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
    }, { userFilter, checked ->
        viewModel.switch(userFilter, checked)
    })

    private val importFiltersLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        viewModel.import(it ?: return@registerForActivityResult)
    }
    private val exportFiltersLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        viewModel.exportAll(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentFiltersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.inflateMenu(R.menu.filters_menu)
        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.create_item) {
                findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToFilterBottomSheet())
            }
            setClickListenerOn(R.id.clear_item) {
                viewModel.clearAll()
            }
            setClickListenerOn(R.id.import_item) {
                importFiltersLauncher.launch(arrayOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "application/json" else "*/*"))
            }
            setClickListenerOn(R.id.export_all_item) {
                exportFiltersLauncher.launch("filters.json")
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