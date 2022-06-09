package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.CrashesAdapter
import com.f0x1d.logfox.databinding.FragmentCrashesBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.logging.Logging
import com.f0x1d.logfox.ui.fragment.base.BaseFragment
import com.f0x1d.logfox.utils.RecyclerViewDivider
import com.f0x1d.logfox.utils.toPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrashesFragment: BaseFragment<FragmentCrashesBinding>() {

    private val adapter = CrashesAdapter {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(it.packageName)
            .setMessage(it.log)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(android.R.string.copy) { dialog, which ->
                requireContext().copyText(it.log)
                dialog.cancel()
            }
            .show()
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCrashesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.crashes_menu)
        binding.toolbar.menu.findItem(R.id.clear_item).setOnMenuItemClickListener {
            Logging.clearCrashes()
            return@setOnMenuItemClickListener true
        }

        binding.crashesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.crashesRecycler.addItemDecoration(RecyclerViewDivider(requireContext(), 80.toPx.toInt()))
        binding.crashesRecycler.adapter = adapter

        Logging.crashesFlow.asLiveData().observe(viewLifecycleOwner) { adapter.elements = it }
    }
}