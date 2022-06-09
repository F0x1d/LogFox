package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.FragmentLogsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.logging.Logging
import com.f0x1d.logfox.logging.model.LogLevel
import com.f0x1d.logfox.ui.dialog.SearchBottomSheet
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>(), SearchBottomSheet.OnSearchClicked {

    override val viewModel by viewModels<LogsViewModel>()

    private val adapter = LogsAdapter {
        requireContext().copyText(it.original)
    }
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
            findItem(R.id.clear_item).setOnMenuItemClickListener {
                Logging.clearLogs()
                if (viewModel.paused())
                    viewModel.resume()
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

        viewModel.distinctiveLogsData.observe(viewLifecycleOwner) {
            if (!viewModel.paused()) {
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
        if (viewModel.paused())
            viewModel.resume()

        viewModel.query = query
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
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}