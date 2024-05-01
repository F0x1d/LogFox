package com.f0x1d.logfox.ui.fragment.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.FiltersAdapter
import com.f0x1d.logfox.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.extensions.canPickJSON
import com.f0x1d.logfox.extensions.showAreYouSureClearDialog
import com.f0x1d.logfox.extensions.showAreYouSureDeleteDialog
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.filters.FiltersViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class FiltersFragment: BaseViewModelFragment<FiltersViewModel, FragmentFiltersBinding>() {

    override val viewModel by viewModels<FiltersViewModel>()

    private val adapter = FiltersAdapter(
        click = {
            findNavController().navigate(
                FiltersFragmentDirections.actionFiltersFragmentToEditFilterFragment(it.id)
            )
        },
        delete = {
            showAreYouSureDeleteDialog {
                viewModel.delete(it)
            }
        },
        checked = { userFilter, checked ->
            viewModel.switch(userFilter, checked)
        }
    )

    private val importFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) {
        viewModel.import(it ?: return@registerForActivityResult)
    }
    private val exportFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) {
        viewModel.exportAll(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFiltersBinding.inflate(inflater, container, false)

    override fun FragmentFiltersBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        addFab.applyInsetter {
            type(navigationBars = true) {
                margin(vertical = true)
            }
        }
        filtersRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        toolbar.setupBackButtonForNavController()
        toolbar.menu.apply {
            setClickListenerOn(R.id.clear_item) {
                showAreYouSureClearDialog {
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

        filtersRecycler.layoutManager = LinearLayoutManager(requireContext())
        filtersRecycler.adapter = adapter

        addFab.setOnClickListener {
            findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToEditFilterFragment())
        }

        viewModel.filters.observe(viewLifecycleOwner) {
            placeholderLayout.root.isVisible = it.isEmpty()

            adapter.submitList(it)
        }
    }
}