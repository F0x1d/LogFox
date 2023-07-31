package com.f0x1d.logfox.ui.fragment

import android.content.SharedPreferences
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
import com.f0x1d.logfox.extensions.sendStopService
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.extensions.startLoggingService
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    private val adapter by lazy {
        LogsAdapter(viewModel.appPreferences) {
            requireContext().copyText(it.original)
            snackbar(R.string.text_copied)
        }
    }
    private var changingState = false

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLogsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.appPreferences.registerListener(this)

        binding.toolbar.inflateMenu(R.menu.logs_menu)
        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.pause_item) {
                viewModel.switchState()
            }
            setClickListenerOn(R.id.search_item) {
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToSearchBottomSheet(viewModel.query.value))
            }
            setClickListenerOn(R.id.filters_item) {
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToFiltersFragment())
            }
            setClickListenerOn(R.id.selected_item) {
                showSelectedDialog()
            }
            setClickListenerOn(R.id.clear_selected_item) {
                adapter.clearSelected()
            }
            setClickListenerOn(R.id.clear_item) {
                viewModel.clearLogs()

                adapter.submitList(null)
                adapter.clearSelected()
            }
            setClickListenerOn(R.id.service_status_item) {
                requireContext().apply {
                    if (viewModel.serviceRunningData.value == true)
                        sendStopService()
                    else
                        startLoggingService()
                }
            }
            setClickListenerOn(R.id.restart_logging_item) {
                viewModel.restartLogging()
            }
            setClickListenerOn(R.id.exit_item) {
                requireContext().sendKillApp()
            }
        }

        binding.logsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.logsRecycler.itemAnimator = null
        binding.logsRecycler.recycledViewPool.setMaxRecycledViews(0, 50)
        binding.logsRecycler.adapter = adapter
        binding.logsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (changingState)
                    return

                if (viewModel.paused.value && !recyclerView.canScrollVertically(1)) {
                    if (viewModel.resumeLoggingWithBottomTouch) viewModel.resume()
                } else
                    viewModel.pause()
            }
        })

        binding.scrollFab.setOnClickListener {
            if (viewModel.resumeLoggingWithBottomTouch)
                viewModel.resume()
            else
                scrollLogToBottom()
        }

        viewModel.logs.observe(viewLifecycleOwner) {
            adapter.submitList(null)
            adapter.submitList(it ?: return@observe) {
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

        viewModel.serviceRunningData.observe(viewLifecycleOwner) { running ->
            binding.toolbar.menu.findItem(R.id.service_status_item).setTitle(if (running) R.string.stop_service else R.string.start_service)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        viewModel.appPreferences.apply {
            if (key == "pref_logs_text_size") adapter.textSize = logsTextSize.toFloat()
            else if (key == "pref_logs_expanded") adapter.logsExpanded = logsExpanded

            if (key?.startsWith("pref_show_log") == true) adapter.logsFormat = showLogValues
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.appPreferences.unregisterListener(this)
    }

    private fun scrollLogToBottom() {
        binding.logsRecycler.stopScroll()
        binding.logsRecycler.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showSelectedDialog() {
        if (adapter.selectedItems.isEmpty()) {
            snackbar(R.string.nothing_is_selected)
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.selected)
            .setIcon(R.drawable.ic_dialog_checklist)
            .setItems(intArrayOf(android.R.string.copy, R.string.extended_copy).fillWithStrings(requireContext())) { dialog, which ->
                val textToCopy = adapter.selectedItems.joinToString("\n") { it.original }
                when (which) {
                    0 -> {
                        requireContext().copyText(textToCopy)
                        snackbar(R.string.text_copied)
                    }
                    1 -> findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToLogsExtendedCopyFragment(textToCopy))
                }
            }
            .setPositiveButton(R.string.close, null)
            .show()
    }
}