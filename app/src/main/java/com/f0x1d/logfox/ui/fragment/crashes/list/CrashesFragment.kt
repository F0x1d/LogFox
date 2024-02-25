package com.f0x1d.logfox.ui.fragment.crashes.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.CrashesAdapter
import com.f0x1d.logfox.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.extensions.context.isHorizontalOrientation
import com.f0x1d.logfox.extensions.dpToPx
import com.f0x1d.logfox.extensions.showAreYouSureClearDialog
import com.f0x1d.logfox.extensions.showAreYouSureDeleteDialog
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.crashes.list.CrashesViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CrashesFragment: BaseViewModelFragment<CrashesViewModel, FragmentCrashesBinding>() {

    override val viewModel by viewModels<CrashesViewModel>()

    private val adapter = CrashesAdapter(
        click = {
            val direction = when (it.count) {
                1 -> CrashesFragmentDirections.actionCrashesFragmentToCrashDetailsFragment(it.lastCrash.id)
                else -> CrashesFragmentDirections.actionCrashesFragmentToAppCrashesFragment(
                    packageName = it.lastCrash.packageName,
                    appName = it.lastCrash.appName
                )
            }
    
            findNavController().navigate(direction)
        }, 
        delete = {
            showAreYouSureDeleteDialog {
                viewModel.deleteCrashesByPackageName(it.lastCrash)
            }
        }
    )

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

        toolbar.menu.setClickListenerOn(R.id.clear_item) {
            showAreYouSureClearDialog {
                viewModel.clearCrashes()
            }
        }

        crashesRecycler.layoutManager = LinearLayoutManager(requireContext())
        crashesRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(), 
                LinearLayoutManager.VERTICAL
            ).apply {
                dividerInsetStart = 80.dpToPx.toInt()
                dividerInsetEnd = 10.dpToPx.toInt()
                isLastItemDecorated = false
            }
        )
        crashesRecycler.adapter = adapter

        viewModel.crashes.observe(viewLifecycleOwner) {
            placeholderLayout.root.isVisible = it.isEmpty()

            adapter.submitList(it)
        }
    }
}