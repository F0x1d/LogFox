package com.f0x1d.logfox.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.CrashesAdapter
import com.f0x1d.logfox.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.ui.activity.CrashDetailsActivity
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.RecyclerViewDivider
import com.f0x1d.logfox.utils.toPx
import com.f0x1d.logfox.viewmodel.CrashesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrashesFragment: BaseViewModelFragment<CrashesViewModel, FragmentCrashesBinding>() {

    override val viewModel by viewModels<CrashesViewModel>()

    private val adapter = CrashesAdapter {
        requireContext().startActivity(Intent(requireContext(), CrashDetailsActivity::class.java).apply {
            putExtra("crash_id", it.id)
        })
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCrashesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.crashes_menu)
        binding.toolbar.menu.findItem(R.id.clear_item).setOnMenuItemClickListener {
            viewModel.clearCrashes()
            return@setOnMenuItemClickListener true
        }

        binding.crashesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.crashesRecycler.addItemDecoration(RecyclerViewDivider(requireContext(), 80.toPx.toInt()))
        binding.crashesRecycler.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { adapter.elements = it ?: return@observe }
    }
}