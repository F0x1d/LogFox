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
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.RecyclerViewDivider
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.viewmodel.crashes.CrashesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrashesFragment: BaseViewModelFragment<CrashesViewModel, FragmentCrashesBinding>() {

    override val viewModel by viewModels<CrashesViewModel>()

    private val adapter = CrashesAdapter(click = {
        findNavController().navigate(CrashesFragmentDirections.actionCrashesFragmentToCrashDetailsActivity(it.id))
    }, delete = {
        viewModel.deleteCrash(it)
    })

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCrashesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.crashes_menu)
        binding.toolbar.menu.setClickListenerOn(R.id.clear_item) {
            viewModel.clearCrashes()
        }

        binding.crashesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.crashesRecycler.addItemDecoration(RecyclerViewDivider(requireContext(), 80.dpToPx.toInt(), 10.dpToPx.toInt()))
        binding.crashesRecycler.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it ?: return@observe)
        }
    }
}