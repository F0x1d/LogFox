package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.FragmentLogsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.sendKillApp
import com.f0x1d.logfox.extensions.toast
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>() {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    private val adapter = LogsAdapter()
    private var changingState = false

    @Inject
    lateinit var appPreferences: AppPreferences

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
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToSearchBottomSheet(viewModel.query))
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.filter_log_levels_item).setOnMenuItemClickListener {
                showFilterDialog()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.selected_item).setOnMenuItemClickListener {
                showSelectedDialog()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.clear_selected_item).setOnMenuItemClickListener {
                adapter.clearSelected()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.clear_item).setOnMenuItemClickListener {
                viewModel.clearLogs()
                adapter.elements = emptyList()
                adapter.clearSelected()
                return@setOnMenuItemClickListener true
            }
            findItem(R.id.exit_item).setOnMenuItemClickListener {
                requireContext().sendKillApp()
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

        appPreferences.asLiveData("pref_logs_text_size", 14).observe(viewLifecycleOwner) {
            adapter.textSize = it.toFloat()
        }

        viewModel.distinctiveData.observe(viewLifecycleOwner) {
            adapter.elements = it ?: return@observe
            scrollLogToBottom()
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
                    1 -> findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToLogsExtendedCopyFragment(textToCopy))
                }
            }
            .setNeutralButton(android.R.string.cancel, null)
            .show()
    }
}