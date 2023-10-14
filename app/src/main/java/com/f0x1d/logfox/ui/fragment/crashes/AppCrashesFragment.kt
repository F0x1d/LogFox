package com.f0x1d.logfox.ui.fragment.crashes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.CrashesAdapter
import com.f0x1d.logfox.databinding.FragmentAppCrashesBinding
import com.f0x1d.logfox.extensions.showAreYouSureDialog
import com.f0x1d.logfox.extensions.views.setupBackButton
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.viewmodel.crashes.AppCrashesViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class AppCrashesFragment: BaseViewModelFragment<AppCrashesViewModel, FragmentAppCrashesBinding>() {

    override val viewModel by viewModels<AppCrashesViewModel>()

    private val adapter = CrashesAdapter(click = {
        findNavController().navigate(
            AppCrashesFragmentDirections.actionAppCrashesFragmentToCrashDetailsActivity(it.lastCrash.id)
        )
    }, delete = {
        showAreYouSureDialog(R.string.delete, R.string.delete_warning) {
            viewModel.deleteCrash(it.lastCrash)
        }
    })

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentAppCrashesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.crashesRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.toolbar.title = viewModel.appName ?: viewModel.packageName
        binding.toolbar.setupBackButton()

        binding.crashesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.crashesRecycler.addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
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