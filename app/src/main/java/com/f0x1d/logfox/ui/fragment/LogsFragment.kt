package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.FragmentLogsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.toast
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.ui.dialog.SearchBottomSheet
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>(), SearchBottomSheet.OnSearchClicked {

    override val viewModel by viewModels<LogsViewModel>()

    private val adapter = LogsAdapter()
    private var changingState = false

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLogsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.logs_menu)
        binding.toolbar.menu.apply {
            findItem(R.id.pause_item).setOnMenuItemClickListener {
                viewModel.switchState()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.search_item).setOnMenuItemClickListener {
                SearchBottomSheet.newInstance(viewModel.query).show(childFragmentManager, "SearchBottomSheet")
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.filter_log_levels_item).setOnMenuItemClickListener {
                showFilterDialog()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.selected).setOnMenuItemClickListener {
                showSelectedDialog()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.clear_selected).setOnMenuItemClickListener {
                adapter.clearSelected()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.clear_item).setOnMenuItemClickListener {
                viewModel.clearLogs()
                adapter.elements = emptyList()
                adapter.clearSelected()
                return@setOnMenuItemClickListener true
            }
        }

        binding.logsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.logsRecycler.itemAnimator = null
        binding.logsRecycler.adapter = adapter
        binding.logsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (changingState)
                    return

                if (viewModel.paused() && !recyclerView.canScrollVertically(1))
                    viewModel.resume()
                else
                    viewModel.pause()
            }
        })

        binding.scrollFab.setOnClickListener { viewModel.resume() }

        viewModel.distinctiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.elements = it
                scrollLogToBottom()
            }
        }

        viewModel.pausedData.observe(viewLifecycleOwner) { paused ->
            changingState = true
            binding.toolbar.menu.findItem(R.id.pause_item).setIcon(if (paused) R.drawable.ic_play else R.drawable.ic_pause)
            if (paused) {
                binding.scrollFab.show()
            } else {
                binding.scrollFab.hide()
                scrollLogToBottom()
            }
            changingState = false
        }
    }

    override fun search(query: String?) {
        viewModel.query(query)
    }

    private fun scrollLogToBottom() {
        binding.logsRecycler.stopScroll()
        binding.logsRecycler.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showFilterDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.filter)
            .setMultiChoiceItems(LogLevel.values().map { it.name }.toTypedArray(), viewModel.currentEnabledLogLevels.checkedItems) { dialog, which, checked ->
                viewModel.filterLevel(which, checked)
            }
            .setNeutralButton(android.R.string.cancel, null)
            .show()
    }

    private fun showSelectedDialog() {
        if (adapter.selectedItems.isEmpty()) {
            requireContext().toast(R.string.nothing_is_selected)
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.selected)
            .setItems(intArrayOf(android.R.string.copy, R.string.extended_copy).fillWithStrings(requireContext())) { dialog, which ->
                val textToCopy = adapter.selectedItems.joinToString("\n") { it.original }
                when (which) {
                    0 -> requireContext().copyText(textToCopy)
                    1 -> findNavController().navigate(R.id.action_logsFragment_to_logsExtendedCopyFragment, bundleOf("content" to textToCopy))
                }
            }
            .setNeutralButton(android.R.string.cancel, null)
            .show()
    }
}