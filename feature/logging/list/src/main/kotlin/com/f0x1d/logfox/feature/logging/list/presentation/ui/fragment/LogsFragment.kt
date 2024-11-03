package com.f0x1d.logfox.feature.logging.list.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.logfox.arch.copyText
import com.f0x1d.logfox.arch.isHorizontalOrientation
import com.f0x1d.logfox.arch.presentation.ui.fragment.BaseFragment
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.logging.api.presentation.LoggingServiceDelegate
import com.f0x1d.logfox.feature.logging.list.R
import com.f0x1d.logfox.feature.logging.list.databinding.FragmentLogsBinding
import com.f0x1d.logfox.feature.logging.list.presentation.LogsViewModel
import com.f0x1d.logfox.feature.logging.list.presentation.adapter.LogsAdapter
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.strings.Plurals
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.view.invalidateNavigationButton
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.ui.view.setupCloseButton
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import javax.inject.Inject

@AndroidEntryPoint
class LogsFragment : BaseFragment<FragmentLogsBinding>() {

    private val viewModel by viewModels<LogsViewModel>()

    @Inject
    lateinit var loggingServiceDelegate: LoggingServiceDelegate

    private val adapter by lazy {
        LogsAdapter(
            textSizeProvider = viewModel::logsTextSize,
            logsExpandedProvider = viewModel::logsExpanded,
            logsFormatProvider = viewModel::logsFormat,
            selectedItem = viewModel::selectLine,
            copyLog = {
                requireContext().copyText(
                    text = viewModel.originalOf(it),
                )
                snackbar(Strings.text_copied)
            },
        )
    }

    private val clearSelectionOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.clearSelection()
        }
    }

    private val exportLogsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        viewModel.exportSelectedLogsTo(it ?: return@registerForActivityResult)
    }

    private var lastPauseEventTimeMillis = 0L

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentLogsBinding.inflate(inflater, container, false)

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
                viewModel.switchState()
            }
            setClickListenerOn(R.id.select_all_item) {
                viewModel.selectAll()
            }
            setClickListenerOn(R.id.search_item) {
                findNavController().navigate(Directions.action_logsFragment_to_searchBottomSheet)
            }
            setClickListenerOn(R.id.filters_item) {
                findNavController().navigate(Directions.action_logsFragment_to_filtersFragment)
            }
            setClickListenerOn(R.id.copy_selected_item) {
                requireContext().copyText(viewModel.selectedItemsContent)
                snackbar(Strings.text_copied)
            }
            setClickListenerOn(R.id.extended_copy_selected_item) {
                findNavController().navigate(Directions.action_logsFragment_to_logsExtendedCopyFragment)
            }
            setClickListenerOn(R.id.selected_to_recording_item) {
                viewModel.selectedToRecording()
                findNavController().navigate(Directions.action_global_recordingsFragment)
            }
            setClickListenerOn(R.id.export_selected_item) {
                exportLogsLauncher.launch(
                    "${viewModel.formatForExport(System.currentTimeMillis())}.log"
                )
            }
            setClickListenerOn(R.id.clear_item) {
                loggingServiceDelegate.clearLogs()
            }
            setClickListenerOn(R.id.restart_logging_item) {
                loggingServiceDelegate.restartLogging()
            }
            setClickListenerOn(R.id.exit_item) {
                loggingServiceDelegate.killService()
            }
        }

        logsRecycler.layoutManager = LinearLayoutManager(requireContext())
        logsRecycler.itemAnimator = null
        logsRecycler.recycledViewPool.setMaxRecycledViews(0, 50)
        logsRecycler.adapter = adapter
        logsRecycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (viewModel.currentState.paused && !recyclerView.canScrollVertically(1)) {
                        val enoughTimePassed = (System.currentTimeMillis() - lastPauseEventTimeMillis) > 300
                        if (viewModel.resumeLoggingWithBottomTouch && enoughTimePassed) viewModel.resume()
                    } else {
                        lastPauseEventTimeMillis = System.currentTimeMillis()
                        viewModel.pause()
                    }
                }
            },
        )

        scrollFab.setOnClickListener {
            if (viewModel.resumeLoggingWithBottomTouch)
                viewModel.resume()
            else
                scrollLogToBottom()
        }

        viewModel.state.collectWithLifecycle { state ->
            processQueryAndFilters(
                query = state.query,
                filters = state.filters,
            )
            processSelectedItems(selectedItems = state.selectedItems)
            processPaused(paused = state.paused)

            updateLogsList(items = state.logs)
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, clearSelectionOnBackPressedCallback)
        }
    }

    private fun FragmentLogsBinding.processQueryAndFilters(query: String?, filters: List<UserFilter>) {
        val subtitle = buildString {
            if (query != null) {
                append(query)

                if (filters.isNotEmpty())
                    append(", ")
            }

            if (filters.isNotEmpty())
                append(resources.getQuantityString(Plurals.filters_count, filters.size, filters.size))
        }

        toolbar.subtitle = subtitle
        placeholderLayout.placeholderText.setText(
            when {
                viewModel.viewingFile -> Strings.no_logs

                subtitle.isEmpty() -> Strings.waiting_for_logs

                else -> Strings.all_logs_were_filtered_out
            }
        )
    }

    private fun FragmentLogsBinding.processSelectedItems(selectedItems: Set<LogLine>) {
        val selecting = selectedItems.isNotEmpty()

        clearSelectionOnBackPressedCallback.isEnabled = selecting

        adapter.selectedItems = selectedItems
        setupToolbarForSelection(selecting, selectedItems.size)
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

    private fun FragmentLogsBinding.setupToolbarForSelection(selecting: Boolean, count: Int) = toolbar.apply {
        val setVisibility = { itemId: Int, visible: Boolean ->
            menu.findItem(itemId).isVisible = visible
        }
        val visibleDuringSelection = { itemId: Int -> setVisibility(itemId, selecting) }
        val invisibleDuringSelection = { itemId: Int -> setVisibility(itemId, !selecting) }
        val visibleOnlyInDefault = { itemId: Int -> setVisibility(itemId, !selecting && !viewModel.viewingFile) }

        visibleOnlyInDefault(R.id.pause_item)
        visibleDuringSelection(R.id.select_all_item)
        invisibleDuringSelection(R.id.search_item)
        invisibleDuringSelection(R.id.filters_item)
        visibleDuringSelection(R.id.selected_item)
        visibleOnlyInDefault(R.id.clear_item)
        visibleOnlyInDefault(R.id.restart_logging_item)

        title = when {
            selecting -> resources.getQuantityString(Plurals.selected_count, count, count)
            viewModel.viewingFile -> viewModel.viewingFileName

            else -> getString(Strings.app_name)
        }

        if (selecting) {
            setupCloseButton()

            setNavigationOnClickListener {
                viewModel.clearSelection()
            }
        } else if (viewModel.viewingFile) {
            setupBackButtonForNavController()
        } else {
            invalidateNavigationButton()
        }
    }

    private fun FragmentLogsBinding.updateLogsList(items: List<LogLine>?) {
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
}
