package com.f0x1d.logfox.feature.crashes.presentation.appcrashes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesCommand
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesState
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesViewModel
import com.f0x1d.logfox.feature.crashes.presentation.common.adapter.CrashesAdapter
import com.f0x1d.logfox.feature.crashes.presentation.databinding.FragmentAppCrashesBinding
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.core.presentation.density.dpToPx
import com.f0x1d.logfox.core.presentation.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.presentation.view.setupBackButtonForNavController
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class AppCrashesFragment : BaseStoreFragment<
    FragmentAppCrashesBinding,
    AppCrashesState,
    AppCrashesCommand,
    AppCrashesSideEffect,
    AppCrashesViewModel,
>() {

    override val viewModel by viewModels<AppCrashesViewModel>()

    private val adapter = CrashesAdapter(
        click = {
            findNavController().navigate(
                resId = Directions.action_appCrashesFragment_to_crashDetailsFragment,
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

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAppCrashesBinding.inflate(inflater, container, false)

    override fun FragmentAppCrashesBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        crashesRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        toolbar.title = viewModel.appName ?: viewModel.packageName
        toolbar.setupBackButtonForNavController()

        crashesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())

            addItemDecoration(
                MaterialDividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                ).apply {
                    dividerInsetStart = 80.dpToPx.toInt()
                    dividerInsetEnd = 10.dpToPx.toInt()
                    isLastItemDecorated = false
                }
            )

            adapter = this@AppCrashesFragment.adapter
        }
    }

    override fun render(state: AppCrashesState) {
        adapter.submitList(state.crashes)
    }

    override fun handleSideEffect(sideEffect: AppCrashesSideEffect) {
        // Business logic side effects are handled by EffectHandler
        // UI side effects would be handled here
    }
}
