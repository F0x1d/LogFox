package com.f0x1d.logfox.feature.logging.presentation.list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.copy.copyText
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.view.invalidateNavigationButton
import com.f0x1d.logfox.core.ui.view.setClickListenerOn
import com.f0x1d.logfox.core.ui.view.setupCloseButton
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.presentation.R
import com.f0x1d.logfox.feature.logging.presentation.databinding.FragmentLogsBinding
import com.f0x1d.logfox.feature.logging.presentation.list.LogsCommand
import com.f0x1d.logfox.feature.logging.presentation.list.LogsSideEffect
import com.f0x1d.logfox.feature.logging.presentation.list.LogsState
import com.f0x1d.logfox.feature.logging.presentation.list.LogsViewModel
import com.f0x1d.logfox.feature.logging.presentation.list.adapter.LogsAdapter
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem
import com.f0x1d.logfox.feature.strings.Plurals
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.navigation.Directions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class LogsFragment :
    BaseStoreFragment<
        FragmentLogsBinding,
        LogsState,
        LogsCommand,
        LogsSideEffect,
        LogsViewModel,
        >() {

    override val viewModel by viewModels<LogsViewModel>()

    private val adapter by lazy {
        LogsAdapter(
            selectedItem = { logLineItem, selected ->
                send(LogsCommand.SelectLine(logLineItem, selected))
            },
            copyLog = { logLineItem ->
                send(LogsCommand.CopyLog(logLineItem))
            },
            createFilter = { logLineItem ->
                send(LogsCommand.CreateFilterFromLog(logLineItem))
            },
        )
    }

    private val clearSelectionOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            send(LogsCommand.ClearSelection)
        }
    }

    private val exportLogsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> send(LogsCommand.ExportSelectedTo(uri)) }
    }

    private var lastPauseEventTimeMillis = 0L

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLogsBinding.inflate(inflater, container, false)

    override fun FragmentLogsBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireContext().isHorizontalOrientation.also { horizontalOrientation ->
            logsRecycler.applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = horizontalOrientation)
                }
            }
            scrollFab.applyInsetter {
                type(navigationBars = true) {
                    margin(vertical = horizontalOrientation)
                }
            }
        }

        toolbar.menu.apply {
            setClickListenerOn(R.id.pause_item) {
                send(LogsCommand.SwitchState)
            }
            setClickListenerOn(R.id.select_all_item) {
                send(LogsCommand.SelectAll)
            }
            setClickListenerOn(R.id.search_item) {
                send(LogsCommand.OpenSearch)
            }
            setClickListenerOn(R.id.filters_item) {
                send(LogsCommand.OpenFiltersScreen)
            }
            setClickListenerOn(R.id.copy_selected_item) {
                send(LogsCommand.CopySelectedLogs)
            }
            setClickListenerOn(R.id.extended_copy_selected_item) {
                send(LogsCommand.OpenExtendedCopy)
            }
            setClickListenerOn(R.id.selected_to_recording_item) {
                send(LogsCommand.SelectedToRecording)
            }
            setClickListenerOn(R.id.export_selected_item) {
                send(LogsCommand.ExportSelectedClicked)
            }
            setClickListenerOn(R.id.clear_item) {
                send(LogsCommand.ClearLogs)
            }
            setClickListenerOn(R.id.restart_logging_item) {
                send(LogsCommand.RestartLogging)
            }
            setClickListenerOn(R.id.exit_item) {
                send(LogsCommand.KillService)
            }
        }

        toolbar.setOnClickListener {
            send(LogsCommand.ToolbarClicked)
        }

        logsRecycler.layoutManager = LinearLayoutManager(requireContext())
        logsRecycler.itemAnimator = null
        logsRecycler.recycledViewPool.setMaxRecycledViews(0, 50)
        logsRecycler.adapter = adapter
        logsRecycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (viewModel.state.value.paused && !recyclerView.canScrollVertically(1)) {
                        val enoughTimePassed =
                            (System.currentTimeMillis() - lastPauseEventTimeMillis) > 300
                        if (viewModel.state.value.resumeLoggingWithBottomTouch && enoughTimePassed) {
                            send(LogsCommand.Resume)
                        }
                    } else {
                        lastPauseEventTimeMillis = System.currentTimeMillis()
                        send(LogsCommand.Pause)
                    }
                }
            },
        )

        scrollFab.setOnClickListener {
            if (viewModel.state.value.resumeLoggingWithBottomTouch) {
                send(LogsCommand.Resume)
            } else {
                scrollLogToBottom()
            }
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, clearSelectionOnBackPressedCallback)
        }
    }

    override fun render(state: LogsState) {
        binding.processQueryAndFilters(
            query = state.query,
            filters = state.filters,
        )
        binding.processSelectedItems(
            selectedItemIds = state.selectedItemIds,
        )
        binding.processPaused(paused = state.paused)

        adapter.textSize = state.logsTextSize
        adapter.logsExpanded = state.logsExpanded

        if (state.logsChanged) {
            binding.updateLogsList(items = state.logs)
        }
    }

    override fun handleSideEffect(sideEffect: LogsSideEffect) {
        when (sideEffect) {
            is LogsSideEffect.NavigateToRecordings -> {
                findNavController().navigate(Directions.action_global_recordingsFragment)
            }

            is LogsSideEffect.NavigateToSearch -> {
                findNavController().navigate(Directions.action_logsFragment_to_searchBottomSheet)
            }

            is LogsSideEffect.OpenFilters -> {
                findNavController().navigate(Directions.action_logsFragment_to_filtersFragment)
            }

            is LogsSideEffect.NavigateToExtendedCopy -> {
                findNavController().navigate(Directions.action_logsFragment_to_logsExtendedCopyFragment)
            }

            is LogsSideEffect.OpenEditFilter -> {
                findNavController().navigate(
                    resId = Directions.action_filtersFragment_to_editFilterFragment,
                    args = bundleOf("filter_id" to sideEffect.filterId),
                )
            }

            is LogsSideEffect.OpenEditFilterFromLogLine -> {
                findNavController().navigate(
                    resId = Directions.action_filtersFragment_to_editFilterFragment,
                    args = bundleOf(
                        "log_uid" to sideEffect.uid,
                        "log_pid" to sideEffect.pid,
                        "log_tid" to sideEffect.tid,
                        "log_package_name" to sideEffect.packageName,
                        "log_tag" to sideEffect.tag,
                        "log_content" to sideEffect.content,
                        "log_level" to sideEffect.level.ordinal,
                    ),
                )
            }

            is LogsSideEffect.CopyText -> {
                requireContext().copyText(sideEffect.text)
                snackbar(Strings.text_copied)
            }

            is LogsSideEffect.LaunchExportPicker -> {
                exportLogsLauncher.launch(sideEffect.filename)
            }

            else -> Unit
        }
    }

    private fun FragmentLogsBinding.processQueryAndFilters(
        query: String?,
        filters: List<UserFilter>,
    ) {
        val subtitle = buildString {
            if (query != null) {
                append(query)

                if (filters.isNotEmpty()) {
                    append(", ")
                }
            }

            if (filters.isNotEmpty()) {
                append(
                    resources.getQuantityString(Plurals.filters_count, filters.size, filters.size),
                )
            }
        }

        toolbar.subtitle = subtitle
        placeholderLayout.placeholderText.setText(
            when {
                subtitle.isEmpty() -> Strings.waiting_for_logs
                else -> Strings.all_logs_were_filtered_out
            },
        )
    }

    private fun FragmentLogsBinding.processSelectedItems(
        selectedItemIds: Set<Long>,
    ) {
        val selecting = selectedItemIds.isNotEmpty()

        clearSelectionOnBackPressedCallback.isEnabled = selecting

        adapter.selecting = selecting
        setupToolbarForSelection(
            selecting = selecting,
            count = selectedItemIds.size,
        )
    }

    private fun FragmentLogsBinding.processPaused(paused: Boolean) {
        toolbar.menu.findItem(R.id.pause_item)
            .setIcon(if (paused) Icons.ic_play else Icons.ic_pause)
            .setTitle(if (paused) Strings.resume else Strings.pause)

        if (paused) {
            scrollFab.show()
        } else {
            scrollFab.hide()
            scrollLogToBottom()
        }
    }

    private fun FragmentLogsBinding.setupToolbarForSelection(
        selecting: Boolean,
        count: Int,
    ) = toolbar.apply {
        val setVisibility = { itemId: Int, visible: Boolean ->
            menu.findItem(itemId).isVisible = visible
        }
        val visibleDuringSelection = { itemId: Int -> setVisibility(itemId, selecting) }
        val invisibleDuringSelection = { itemId: Int -> setVisibility(itemId, !selecting) }
        val visibleOnlyInDefault = { itemId: Int ->
            setVisibility(
                itemId,
                !selecting,
            )
        }

        visibleOnlyInDefault(R.id.pause_item)
        visibleDuringSelection(R.id.select_all_item)
        invisibleDuringSelection(R.id.search_item)
        invisibleDuringSelection(R.id.filters_item)
        visibleDuringSelection(R.id.selected_item)
        visibleOnlyInDefault(R.id.clear_item)
        visibleOnlyInDefault(R.id.restart_logging_item)
        visibleOnlyInDefault(R.id.exit_item)

        title = when {
            selecting -> resources.getQuantityString(Plurals.selected_count, count, count)
            else -> getString(Strings.app_name)
        }

        if (selecting) {
            setupCloseButton()

            setNavigationOnClickListener {
                send(LogsCommand.ClearSelection)
            }
        } else {
            invalidateNavigationButton()
        }
    }

    private fun FragmentLogsBinding.updateLogsList(items: List<LogLineItem>?) {
        placeholderLayout.root.apply {
            if (items?.isEmpty() != false) {
                animate()
                    .alpha(1f)
                    .setStartDelay(1000)
                    .setDuration(200)
            } else {
                animate().cancel()
                alpha = 0f
            }
        }

        adapter.submitList(null)
        adapter.submitList(items) {
            scrollLogToBottom()
        }
    }

    private fun FragmentLogsBinding.scrollLogToBottom() {
        logsRecycler.stopScroll()
        logsRecycler.scrollToPosition(adapter.itemCount - 1)
    }

    private fun snackbar(messageRes: Int) {
        Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
    }
}
