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
import com.f0x1d.logfox.feature.crashes.presentation.R
import com.f0x1d.logfox.feature.crashes.presentation.common.adapter.CrashesAdapter
import com.f0x1d.logfox.feature.crashes.presentation.databinding.DialogSortingBinding
import com.f0x1d.logfox.feature.crashes.presentation.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.feature.crashes.presentation.databinding.ItemSortBinding
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesCommand
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesState
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesViewModel
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.core.presentation.density.dpToPx
import com.f0x1d.logfox.core.presentation.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.core.presentation.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.presentation.view.setClickListenerOn
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class CrashesFragment : BaseStoreFragment<
    FragmentCrashesBinding,
    CrashesState,
    CrashesCommand,
    CrashesSideEffect,
    CrashesViewModel,
>() {

    override val viewModel by hiltNavGraphViewModels<CrashesViewModel>(Directions.crashesFragment)

    private val adapter = CrashesAdapter(
        click = {
            val (direction, args) = when (it.count) {
                1 -> Directions.action_crashesFragment_to_crashDetailsFragment to bundleOf(
                    "crash_id" to it.lastCrash.id,
                )

                else -> Directions.action_crashesFragment_to_appCrashesFragment to bundleOf(
                    "package_name" to it.lastCrash.packageName,
                    "app_name" to it.lastCrash.appName,
                )
            }

            findNavController().navigate(direction, args)
        },
        delete = {
            showAreYouSureDeleteDialog {
                viewModel.deleteCrashesByPackageName(it.lastCrash)
            }
        },
    )
    private val searchedAdapter = CrashesAdapter(
        click = {
            findNavController().navigate(
                resId = Directions.action_crashesFragment_to_crashDetailsFragment,
                args = bundleOf(
                    "crash_id" to it.lastCrash.id,
                ),
            )
        },
        delete = {
            showAreYouSureDeleteDialog {
                viewModel.deleteCrash(it.lastCrash)
            }
        },
    )

    private val closeSearchOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.searchView.hide()
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrashesBinding.inflate(inflater, container, false)

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
                findNavController().navigate(Directions.action_crashesFragment_to_appsPickerFragment)
            }
            setClickListenerOn(R.id.clear_item) {
                showAreYouSureClearDialog {
                    viewModel.clearCrashes()
                }
            }
        }

        searchView.apply {
            editText.doAfterTextChanged { text ->
                viewModel.updateQuery(text?.toString().orEmpty())
            }

            addTransitionListener { _, _, newState ->
                closeSearchOnBackPressedCallback.isEnabled = newState == SearchView.TransitionState.SHOWN
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
                }
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

    override fun render(state: CrashesState) {
        binding.placeholderLayout.root.isVisible = state.crashes.isEmpty()

        adapter.submitList(state.crashes)
        searchedAdapter.submitList(state.searchedCrashes)
    }

    override fun handleSideEffect(sideEffect: CrashesSideEffect) {
        // Business logic side effects are handled by EffectHandler
        // UI side effects would be handled here
    }

    private fun showSortDialog() {
        var selectedSortType = viewModel.currentState.currentSort
        var sortInReversedOrder = viewModel.currentState.sortInReversedOrder

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

                if (button.tag == viewModel.currentState.currentSort) {
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
                viewModel.updateSort(
                    sortType = selectedSortType,
                    sortInReversedOrder = sortInReversedOrder,
                )
            }
            .show()
    }
}
