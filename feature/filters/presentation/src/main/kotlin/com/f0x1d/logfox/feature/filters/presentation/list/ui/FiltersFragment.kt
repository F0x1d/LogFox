package com.f0x1d.logfox.feature.filters.presentation.list.ui

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
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.ui.view.setClickListenerOn
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.feature.filters.presentation.R
import com.f0x1d.logfox.feature.filters.presentation.databinding.FragmentFiltersBinding
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersCommand
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersSideEffect
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersState
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersViewModel
import com.f0x1d.logfox.feature.filters.presentation.list.adapter.FiltersAdapter
import com.f0x1d.logfox.navigation.Directions
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class FiltersFragment :
    BaseStoreFragment<
        FragmentFiltersBinding,
        FiltersState,
        FiltersCommand,
        FiltersSideEffect,
        FiltersViewModel,
        >() {

    override val viewModel by viewModels<FiltersViewModel>()

    private val adapter = FiltersAdapter(
        click = {
            send(FiltersCommand.OpenFilter(it.id))
        },
        delete = {
            showAreYouSureDeleteDialog {
                send(FiltersCommand.Delete(it))
            }
        },
        checked = { userFilter, checked ->
            send(FiltersCommand.Switch(userFilter, checked))
        },
    )

    private val importFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { send(FiltersCommand.Import(it)) }
    }

    private val exportFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let { send(FiltersCommand.ExportAll(it)) }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentFiltersBinding.inflate(inflater, container, false)

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
                    send(FiltersCommand.ClearAll)
                }
            }
            setClickListenerOn(R.id.import_item) {
                importFiltersLauncher.launch(arrayOf("application/json", "*/*"))
            }
            setClickListenerOn(R.id.export_all_item) {
                exportFiltersLauncher.launch("filters.json")
            }
        }

        filtersRecycler.layoutManager = LinearLayoutManager(requireContext())
        filtersRecycler.adapter = adapter

        addFab.setOnClickListener {
            send(FiltersCommand.CreateNewFilter)
        }
    }

    override fun render(state: FiltersState) {
        binding.placeholderLayout.root.isVisible = state.filters.isEmpty()
        adapter.submitList(state.filters)
    }

    override fun handleSideEffect(sideEffect: FiltersSideEffect) {
        when (sideEffect) {
            is FiltersSideEffect.NavigateToEditFilter -> {
                findNavController().navigate(
                    resId = Directions.action_filtersFragment_to_editFilterFragment,
                    args = bundleOf("filter_id" to sideEffect.filterId),
                )
            }

            is FiltersSideEffect.NavigateToCreateFilter -> {
                findNavController().navigate(Directions.action_filtersFragment_to_editFilterFragment)
            }

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }
}
