package com.f0x1d.logfox.feature.crashes.ui.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.context.isHorizontalOrientation
import com.f0x1d.logfox.feature.crashes.R
import com.f0x1d.logfox.feature.crashes.adapter.CrashesAdapter
import com.f0x1d.logfox.feature.crashes.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.feature.crashes.viewmodel.list.CrashesViewModel
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.ui.density.dpToPx
import com.f0x1d.logfox.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CrashesFragment: BaseViewModelFragment<CrashesViewModel, FragmentCrashesBinding>() {

    override val viewModel by viewModels<CrashesViewModel>()

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

        searchBar.menu.setClickListenerOn(R.id.clear_item) {
            showAreYouSureClearDialog {
                viewModel.clearCrashes()
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

        viewModel.crashes.collectWithLifecycle {
            binding.placeholderLayout.root.isVisible = it.isEmpty()

            adapter.submitList(it)
        }
        viewModel.searchedCrashes.collectWithLifecycle {
            searchedAdapter.submitList(it)
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }
}
