package com.f0x1d.logfox.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.FragmentLogsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.isHorizontalOrientation
import com.f0x1d.logfox.extensions.readFileName
import com.f0x1d.logfox.extensions.sendKillApp
import com.f0x1d.logfox.extensions.sendStopService
import com.f0x1d.logfox.extensions.startLoggingService
import com.f0x1d.logfox.extensions.views.widgets.invalidateNavigationButton
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupCloseButton
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.fillWithStrings
import com.f0x1d.logfox.viewmodel.LogsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    private val adapter by lazy {
        LogsAdapter(viewModel.appPreferences, selectedItem = viewModel::selectLine, copyLog = {
            requireContext().copyText(it.original)
            snackbar(R.string.text_copied)
        })
    }
    private var changingState = false

    private val stopViewingFileBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.stopViewingFile()
        }
    }
    private val clearSelectionBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.selectedItems.update {
                emptyList()
            }
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLogsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.appPreferences.registerListener(this)

        requireContext().isHorizontalOrientation.also { horizontalOrientation ->
            binding.logsRecycler.applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = horizontalOrientation)
                }
            }
            binding.scrollFab.applyInsetter {
                type(navigationBars = true) {
                    margin(vertical = horizontalOrientation)
                }
            }
        }

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
            setClickListenerOn(R.id.clear_item) {
                viewModel.clearLogs()

                adapter.submitList(null)
                viewModel.selectedItems.update { emptyList() }
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
        binding.toolbar.setNavigationOnClickListener {
            viewModel.apply {
                if (selectedItems.value.isNotEmpty()) selectedItems.update {
                    emptyList()
                } else if (viewModel.viewingFile.value)
                    viewModel.stopViewingFile()
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

        viewModel.viewingFile.asLiveData().observe(viewLifecycleOwner) {
            stopViewingFileBackPressedCallback.isEnabled = it
            setupToolbarForFile(it)
        }

        viewModel.selectedItems.asLiveData().observe(viewLifecycleOwner) {
            val selecting = it.isNotEmpty()

            clearSelectionBackPressedCallback.isEnabled = selecting
            adapter.selectedItems = it

            setupToolbarForSelection(selecting, it.size)
        }

        viewModel.logs.observe(viewLifecycleOwner) {
            // TODO: Understand why all ListAdapters stop working after 15 minutes without submitting null

            adapter.submitList(null)
            adapter.submitList(it ?: return@observe) {
                scrollLogToBottom()
            }
        }

        viewModel.paused.asLiveData().observe(viewLifecycleOwner) { paused ->
            changingState = true
            binding.toolbar.menu.findItem(R.id.pause_item)
                .setIcon(if (paused) R.drawable.ic_play else R.drawable.ic_pause)
                .setTitle(if (paused) R.string.resume else R.string.pause)
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

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, stopViewingFileBackPressedCallback)
            addCallback(viewLifecycleOwner, clearSelectionBackPressedCallback)
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

    private fun setupToolbarForFile(viewing: Boolean) = binding.toolbar.apply {
        menu.findItem(R.id.pause_item).isVisible = !viewing

        title = when (viewing) {
            true -> viewModel.fileUri?.readFileName(requireContext())

            else -> getString(R.string.app_name)
        }

        if (viewing) setupCloseButton()
        else invalidateNavigationButton()
    }

    private fun setupToolbarForSelection(selecting: Boolean, count: Int) = binding.toolbar.apply {
        val setVisibility = { itemId: Int, visible: Boolean ->
            menu.findItem(itemId).isVisible = visible
        }
        val visibleDuringSelection = { itemId: Int -> setVisibility(itemId, selecting) }
        val invisibleDuringSelection = { itemId: Int -> setVisibility(itemId, !selecting) }

        invisibleDuringSelection(R.id.search_item)
        invisibleDuringSelection(R.id.filters_item)
        visibleDuringSelection(R.id.selected_item)
        invisibleDuringSelection(R.id.clear_item)
        invisibleDuringSelection(R.id.service_status_item)
        invisibleDuringSelection(R.id.restart_logging_item)

        title = when {
            selecting -> count.toString()
            viewModel.viewingFile.value -> viewModel.fileUri?.readFileName(requireContext())

            else -> getString(R.string.app_name)
        }

        if (selecting) setupCloseButton()
        else if (!viewModel.viewingFile.value) invalidateNavigationButton()
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