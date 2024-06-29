package com.f0x1d.logfox.feature.filters.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.arch.canPickJSON
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.feature.filters.R
import com.f0x1d.logfox.feature.filters.adapter.FiltersAdapter
import com.f0x1d.logfox.feature.filters.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.feature.filters.viewmodel.FiltersViewModel
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class FiltersFragment: BaseViewModelFragment<FiltersViewModel, FragmentFiltersBinding>() {

    override val viewModel by viewModels<FiltersViewModel>()

    private val adapter = FiltersAdapter(
        click = {
            findNavController().navigate(
                resId = Directions.action_filtersFragment_to_editFilterFragment,
                args = bundleOf("filter_id" to it.id),
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
        ActivityResultContracts.OpenDocument(),
    ) {
        viewModel.import(it ?: return@registerForActivityResult)
    }
    private val exportFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) {
        viewModel.exportAll(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
            findNavController().navigate(Directions.action_editFilterFragment_to_chooseAppFragment)
        }

        viewModel.filters.collectWithLifecycle {
            placeholderLayout.root.isVisible = it.isEmpty()

            adapter.submitList(it)
        }
    }
}
