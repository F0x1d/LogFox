package com.f0x1d.logfox.feature.crashes.apps.list.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.arch.presentation.ui.fragment.BaseFragment
import com.f0x1d.logfox.feature.crashes.apps.list.databinding.FragmentAppCrashesBinding
import com.f0x1d.logfox.feature.crashes.apps.list.presentation.AppCrashesViewModel
import com.f0x1d.logfox.feature.crashes.common.presentation.adapter.CrashesAdapter
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.ui.density.dpToPx
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class AppCrashesFragment: BaseFragment<FragmentAppCrashesBinding>() {

    private val viewModel by viewModels<AppCrashesViewModel>()

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

        viewModel.state.collectWithLifecycle {
            adapter.submitList(it.crashes)
        }
    }
}
