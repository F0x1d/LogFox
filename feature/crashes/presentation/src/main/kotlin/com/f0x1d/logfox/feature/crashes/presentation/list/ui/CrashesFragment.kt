package com.f0x1d.logfox.feature.crashes.presentation.list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.ui.view.density.dpToPx
import com.f0x1d.logfox.core.ui.view.setClickListenerOn
import com.f0x1d.logfox.feature.crashes.presentation.R
import com.f0x1d.logfox.feature.crashes.presentation.common.adapter.CrashesAdapter
import com.f0x1d.logfox.feature.crashes.presentation.databinding.DialogSortingBinding
import com.f0x1d.logfox.feature.crashes.presentation.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.feature.crashes.presentation.databinding.ItemSortBinding
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesCommand
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesState
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesViewModel
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesViewState
import com.f0x1d.logfox.feature.navigation.api.Directions
import com.f0x1d.logfox.feature.preferences.api.CrashesSort
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class CrashesFragment :
    BaseStoreFragment<
        FragmentCrashesBinding,
        CrashesViewState,
        CrashesState,
        CrashesCommand,
        CrashesSideEffect,
        CrashesViewModel,
        >() {

    override val viewModel by hiltNavGraphViewModels<CrashesViewModel>(Directions.crashesFragment)

    private val adapter = CrashesAdapter(
        click = {
            send(
                CrashesCommand.CrashClicked(
                    crashId = it.lastCrashId,
                    count = it.count,
                    packageName = it.packageName,
                    appName = it.appName,
                ),
            )
        },
        delete = {
            showAreYouSureDeleteDialog {
                send(CrashesCommand.DeleteCrashesByPackageName(it.packageName))
            }
        },
    )
    private val searchedAdapter = CrashesAdapter(
        click = {
            send(CrashesCommand.SearchedCrashClicked(it.lastCrashId))
        },
        delete = {
            showAreYouSureDeleteDialog {
                send(CrashesCommand.DeleteCrash(it.lastCrashId))
            }
        },
    )

    private val closeSearchOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.searchView.hide()
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCrashesBinding.inflate(inflater, container, false)

    override fun FragmentCrashesBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        crashesRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = requireContext().isHorizontalOrientation)
            }
        }
        searchedCrashesRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = requireContext().isHorizontalOrientation)
            }
            type(ime = true) {
                padding(vertical = requireContext().isHorizontalOrientation)
            }
        }

        searchBar.menu.apply {
            setClickListenerOn(R.id.sort_item) {
                showSortDialog()
            }
            setClickListenerOn(R.id.blacklist_item) {
                send(CrashesCommand.OpenBlacklist)
            }
            setClickListenerOn(R.id.clear_item) {
                showAreYouSureClearDialog {
                    send(CrashesCommand.ClearCrashes)
                }
            }
        }

        searchView.apply {
            editText.doAfterTextChanged { text ->
                send(CrashesCommand.UpdateQuery(text?.toString().orEmpty()))
            }

            addTransitionListener { _, _, newState ->
                closeSearchOnBackPressedCallback.isEnabled =
                    newState == SearchView.TransitionState.SHOWN
            }
        }

        crashesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())

            addItemDecoration(
                MaterialDividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                ).apply {
                    dividerInsetStart = 80.dpToPx.toInt()
                    dividerInsetEnd = 10.dpToPx.toInt()
                    isLastItemDecorated = false
                },
            )

            adapter = this@CrashesFragment.adapter
        }

        searchedCrashesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchedAdapter
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }

    override fun render(state: CrashesViewState) {
        binding.placeholderLayout.root.isVisible = state.crashes.isEmpty()

        adapter.submitList(state.crashes)
        searchedAdapter.submitList(state.searchedCrashes)
    }

    override fun handleSideEffect(sideEffect: CrashesSideEffect) {
        when (sideEffect) {
            is CrashesSideEffect.NavigateToCrashDetails -> {
                findNavController().navigate(
                    resId = Directions.action_crashesFragment_to_crashDetailsFragment,
                    args = bundleOf("crash_id" to sideEffect.crashId),
                )
            }

            is CrashesSideEffect.NavigateToAppCrashes -> {
                findNavController().navigate(
                    resId = Directions.action_crashesFragment_to_appCrashesFragment,
                    args = bundleOf(
                        "package_name" to sideEffect.packageName,
                        "app_name" to sideEffect.appName,
                    ),
                )
            }

            is CrashesSideEffect.NavigateToBlacklist -> {
                findNavController().navigate(Directions.action_crashesFragment_to_appsPickerFragment)
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }

    private fun showSortDialog() {
        var selectedSortType = viewModel.state.value.currentSort
        var sortInReversedOrder = viewModel.state.value.sortInReversedOrder

        val dialogBinding = DialogSortingBinding.inflate(layoutInflater)
        CrashesSort.entries.map { type ->
            ItemSortBinding.inflate(layoutInflater).root.apply {
                id = View.generateViewId()
                text = getString(type.titleRes)
                tag = type

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedSortType = type
                    }
                }
            }
        }.forEach { button ->
            dialogBinding.rgSorting.apply {
                addView(button)

                if (button.tag == viewModel.state.value.currentSort) {
                    check(button.id)
                }
            }
        }

        dialogBinding.reverseSwitch.apply {
            isChecked = sortInReversedOrder
            setOnCheckedChangeListener { _, isChecked -> sortInReversedOrder = isChecked }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(Strings.sort)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                send(CrashesCommand.UpdateSort(
                    sortType = selectedSortType,
                    sortInReversedOrder = sortInReversedOrder,
                ))
            }
            .show()
    }
}
