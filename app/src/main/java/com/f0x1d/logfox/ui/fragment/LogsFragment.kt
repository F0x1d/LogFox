package com.f0x1d.logfox.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.FragmentLogsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.sendKillApp
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.extensions.toast
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.LogLinesDiffUtilCallback
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    private val adapter = LogsAdapter()
    private var changingState = false

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLogsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences.registerListener(this)

        binding.toolbar.inflateMenu(R.menu.logs_menu)
        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.pause_item) {
                viewModel.switchState()
            }
            setClickListenerOn(R.id.search_item) {
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToSearchBottomSheet(viewModel.query))
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
                adapter.elements = emptyList()
                adapter.clearSelected()
            }
            setClickListenerOn(R.id.exit_item) {
                requireContext().sendKillApp()
            }
        }

        binding.logsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.logsRecycler.itemAnimator = null
        binding.logsRecycler.recycledViewPool.setMaxRecycledViews(0, 40)
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

        adapter.textSize = appPreferences.logsTextSize.toFloat()
        adapter.logsExpanded = appPreferences.logsExpanded
        adapter.logsFormat = appPreferences.showLogValues

        viewModel.distinctiveData.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            val manyDiffs = it.size - adapter.elements.size >= 100

            if (manyDiffs) {
                adapter.elements = it
                adapter.notifyDataSetChanged()
            } else {
                DiffUtil.calculateDiff(LogLinesDiffUtilCallback(adapter.elements, it ?: return@observe), false).apply {
                    adapter.elements = it
                    dispatchUpdatesTo(adapter)

                    scrollLogToBottom()
                }
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        if (key == "pref_logs_text_size") adapter.textSize = appPreferences.logsTextSize.toFloat()
        if (key == "pref_logs_expanded") adapter.logsExpanded = appPreferences.logsExpanded
        if (key.startsWith("pref_show_log")) adapter.logsFormat = appPreferences.showLogValues
    }

    override fun onDestroy() {
        super.onDestroy()
        appPreferences.unregisterListener(this)
    }

    private fun scrollLogToBottom() {
        binding.logsRecycler.stopScroll()
        binding.logsRecycler.scrollToPosition(adapter.itemCount - 1)
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