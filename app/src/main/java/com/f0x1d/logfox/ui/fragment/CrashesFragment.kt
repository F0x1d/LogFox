package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.CrashesAdapter
import com.f0x1d.logfox.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.extensions.isHorizontalOrientation
import com.f0x1d.logfox.extensions.paddingRelative
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.extensions.showAreYouSureDialog
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.viewmodel.crashes.CrashesViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CrashesFragment : BaseViewModelFragment<CrashesViewModel, FragmentCrashesBinding>() {

    override val viewModel by viewModels<CrashesViewModel>()

    private val adapter = CrashesAdapter(click = {
        findNavController().navigate(
            CrashesFragmentDirections.actionCrashesFragmentToCrashDetailsActivity(
                it.id
            )
        )
    }, delete = {
        showAreYouSureDialog {
            viewModel.deleteCrash(it)
        }
    })

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCrashesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().also {
            binding.appBarLayout.applyInsetter {
                type(statusBars = true, navigationBars = true, displayCutout = true) {
                    paddingRelative(it, start = !it.isHorizontalOrientation, top = true, end = true)
                }
            }
            binding.crashesRecycler.applyInsetter {
                type(navigationBars = true, displayCutout = true) {
                    paddingRelative(
                        it,
                        start = !it.isHorizontalOrientation,
                        bottom = it.isHorizontalOrientation,
                        end = true
                    )
                }
            }
        }


        binding.toolbar.inflateMenu(R.menu.crashes_menu)
        binding.toolbar.menu.setClickListenerOn(R.id.clear_item) {
            showAreYouSureDialog {
                viewModel.clearCrashes()
            }
        }

        binding.crashesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.crashesRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                dividerInsetStart = 80.dpToPx.toInt()
                dividerInsetEnd = 10.dpToPx.toInt()
                isLastItemDecorated = false
            })
        binding.crashesRecycler.adapter = adapter

        viewModel.crashes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}