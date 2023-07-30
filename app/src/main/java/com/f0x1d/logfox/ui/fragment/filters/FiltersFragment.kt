package com.f0x1d.logfox.ui.fragment.filters

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.FiltersAdapter
import com.f0x1d.logfox.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.extensions.applyInsets
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.f0x1d.logfox.viewmodel.filters.FiltersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersFragment: BaseViewModelFragment<FiltersViewModel, FragmentFiltersBinding>() {

    override val viewModel by viewModels<FiltersViewModel>()
    private val logsViewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    private val adapter = FiltersAdapter(click = {
        findNavController().navigate(
            FiltersFragmentDirections.actionFiltersFragmentToEditFilterFragment(
                it.id
            )
        )
    }, delete = {
        viewModel.delete(it)
    }, checked = { userFilter, checked ->
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

        applyInsets(view) { insets ->
            binding.addFab.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = 10.dpToPx.toInt() + insets.bottom
            }

            binding.filtersRecycler.updatePadding(bottom = 71.dpToPx.toInt() + insets.bottom)
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.toolbar.inflateMenu(R.menu.filters_menu)
        binding.toolbar.menu.apply {
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

        binding.addFab.setOnClickListener {
            findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToEditFilterFragment())
        }

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it ?: return@observe)
            logsViewModel.recollect()
        }
    }
}