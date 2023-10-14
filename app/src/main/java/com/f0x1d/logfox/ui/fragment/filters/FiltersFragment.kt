package com.f0x1d.logfox.ui.fragment.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.FiltersAdapter
import com.f0x1d.logfox.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.extensions.canPickJSON
import com.f0x1d.logfox.extensions.showAreYouSureDialog
import com.f0x1d.logfox.extensions.views.setClickListenerOn
import com.f0x1d.logfox.extensions.views.setupBackButton
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.filters.FiltersViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class FiltersFragment: BaseViewModelFragment<FiltersViewModel, FragmentFiltersBinding>() {

    override val viewModel by viewModels<FiltersViewModel>()

    private val adapter = FiltersAdapter(click = {
        findNavController().navigate(
            FiltersFragmentDirections.actionFiltersFragmentToEditFilterFragment(it.id)
        )
    }, delete = {
        showAreYouSureDialog(R.string.delete, R.string.delete_warning) {
            viewModel.delete(it)
        }
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

        binding.addFab.applyInsetter {
            type(navigationBars = true) {
                margin(vertical = true)
            }
        }
        binding.filtersRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.toolbar.setupBackButton()
        binding.toolbar.inflateMenu(R.menu.filters_menu)
        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.clear_item) {
                showAreYouSureDialog(R.string.clear, R.string.clear_warning) {
                    viewModel.clearAll()
                }
            }
            setClickListenerOn(R.id.import_item) {
                importFiltersLauncher.launch(arrayOf(if (canPickJSON) "application/json" else "*/*"))
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

        viewModel.filters.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}