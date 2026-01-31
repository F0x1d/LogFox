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
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.ui.view.density.dpToPx
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesCommand
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesState
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesViewModel
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesViewState
import com.f0x1d.logfox.feature.crashes.presentation.common.adapter.CrashesAdapter
import com.f0x1d.logfox.feature.crashes.presentation.databinding.FragmentAppCrashesBinding
import com.f0x1d.logfox.feature.navigation.api.Directions
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class AppCrashesFragment :
    BaseStoreFragment<
        FragmentAppCrashesBinding,
        AppCrashesViewState,
        AppCrashesState,
        AppCrashesCommand,
        AppCrashesSideEffect,
        AppCrashesViewModel,
        >() {

    override val viewModel by viewModels<AppCrashesViewModel>()

    private val adapter = CrashesAdapter(
        click = {
            send(AppCrashesCommand.CrashClicked(it.lastCrashId))
        },
        delete = {
            showAreYouSureDeleteDialog {
                send(AppCrashesCommand.DeleteCrash(it.lastCrashId))
            }
        },
    )

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentAppCrashesBinding.inflate(inflater, container, false)

    override fun FragmentAppCrashesBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        crashesRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        toolbar.setupBackButtonForNavController()

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

            adapter = this@AppCrashesFragment.adapter
        }
    }

    override fun render(state: AppCrashesViewState) {
        binding.toolbar.title = state.appName ?: state.packageName
        adapter.submitList(state.crashes)
    }

    override fun handleSideEffect(sideEffect: AppCrashesSideEffect) {
        when (sideEffect) {
            is AppCrashesSideEffect.NavigateToCrashDetails -> {
                findNavController().navigate(
                    resId = Directions.action_appCrashesFragment_to_crashDetailsFragment,
                    args = bundleOf("crash_id" to sideEffect.crashId),
                )
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
