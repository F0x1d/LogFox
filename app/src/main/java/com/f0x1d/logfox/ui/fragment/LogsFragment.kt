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
import com.f0x1d.logfox.extensions.sendKillApp
import com.f0x1d.logfox.extensions.sendStopService
import com.f0x1d.logfox.extensions.startLoggingService
import com.f0x1d.logfox.extensions.views.widgets.invalidateNavigationButton
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.extensions.views.widgets.setupCloseButton
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.LogsViewModel
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

    private val clearSelectionOnBackPressedCallback = object : OnBackPressedCallback(false) {
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

        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.pause_item) {
                viewModel.switchState()
            }
            setClickListenerOn(R.id.search_item) {
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToSearchBottomSheet())
            }
            setClickListenerOn(R.id.filters_item) {
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToFiltersFragment())
            }
            setClickListenerOn(R.id.copy_selected_item) {
                requireContext().copyText(viewModel.selectedItemsContent)
                snackbar(R.string.text_copied)
            }
            setClickListenerOn(R.id.extended_copy_selected_item) {
                findNavController().navigate(LogsFragmentDirections.actionLogsFragmentToLogsExtendedCopyFragment(viewModel.selectedItemsContent))
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

        viewModel.selectedItems.asLiveData().observe(viewLifecycleOwner) {
            val selecting = it.isNotEmpty()

            clearSelectionOnBackPressedCallback.isEnabled = selecting

            adapter.selectedItems = it
            setupToolbarForSelection(selecting, it.size)
        }

        viewModel.logs.observe(viewLifecycleOwner) {
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
            addCallback(viewLifecycleOwner, clearSelectionOnBackPressedCallback)
        }
    }

    private fun setupToolbarForSelection(selecting: Boolean, count: Int) = binding.toolbar.apply {
        val setVisibility = { itemId: Int, visible: Boolean ->
            menu.findItem(itemId).isVisible = visible
        }
        val visibleDuringSelection = { itemId: Int -> setVisibility(itemId, selecting) }
        val invisibleDuringSelection = { itemId: Int -> setVisibility(itemId, !selecting) }
        val visibleOnlyInDefault = { itemId: Int -> setVisibility(itemId, !selecting && !viewModel.viewingFile) }

        visibleOnlyInDefault(R.id.pause_item)
        invisibleDuringSelection(R.id.search_item)
        invisibleDuringSelection(R.id.filters_item)
        visibleDuringSelection(R.id.selected_item)
        visibleOnlyInDefault(R.id.clear_item)
        visibleOnlyInDefault(R.id.service_status_item)
        visibleOnlyInDefault(R.id.restart_logging_item)

        title = when {
            selecting -> resources.getQuantityString(R.plurals.selected_count, count, count)
            viewModel.viewingFile -> viewModel.viewingFileName

            else -> getString(R.string.app_name)
        }

        if (selecting) {
            setupCloseButton()

            setNavigationOnClickListener {
                viewModel.selectedItems.update {
                    emptyList()
                }
            }
        } else if (viewModel.viewingFile)
            setupBackButtonForNavController()

        else invalidateNavigationButton()
    }

    private fun scrollLogToBottom() {
        binding.logsRecycler.stopScroll()
        binding.logsRecycler.scrollToPosition(adapter.itemCount - 1)
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
}