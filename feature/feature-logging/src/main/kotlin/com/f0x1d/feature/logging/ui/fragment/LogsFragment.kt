package com.f0x1d.feature.logging.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f0x1d.feature.logging.adapter.LogsAdapter
import com.f0x1d.feature.logging.service.LoggingService
import com.f0x1d.feature.logging.viewmodel.LogsViewModel
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.context.copyText
import com.f0x1d.logfox.context.isHorizontalOrientation
import com.f0x1d.logfox.context.sendService
import com.f0x1d.logfox.feature.logging.R
import com.f0x1d.logfox.feature.logging.databinding.FragmentLogsBinding
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
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class LogsFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsBinding>() {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(Directions.logsFragment)

    private val adapter by lazy {
        LogsAdapter(
            appPreferences = viewModel.appPreferences,
            selectedItem = viewModel::selectLine,
            copyLog = {
                requireContext().copyText(
                    viewModel.appPreferences.originalOf(
                        logLine = it,
                        formatDate = viewModel.dateTimeFormatter::formatDate,
                        formatTime = viewModel.dateTimeFormatter::formatTime,
                    )
                )
                snackbar(Strings.text_copied)
            },
        )
    }
    private var changingState = false

    private val clearSelectionOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.selectedItems.update {
                emptySet()
            }
        }
    }

    private val exportLogsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*")
    ) {
        viewModel.exportSelectedLogsTo(it ?: return@registerForActivityResult)
    }

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
                findNavController().navigate(
                    resId = Directions.action_logsFragment_to_logsExtendedCopyFragment,
                    args = bundleOf(
                        "content" to viewModel.selectedItemsContent,
                    ),
                )
            }
            setClickListenerOn(R.id.selected_to_recording_item) {
                viewModel.selectedToRecording()
                findNavController().navigate(Directions.action_global_recordingsFragment)
            }
            setClickListenerOn(R.id.export_selected_item) {
                exportLogsLauncher.launch(
                    "${viewModel.dateTimeFormatter.formatForExport(System.currentTimeMillis())}.log"
                )
            }
            setClickListenerOn(R.id.clear_item) {
                requireContext().sendService<LoggingService>(action = LoggingService.ACTION_CLEAR_LOGS)
                updateLogsList(null)
            }
            setClickListenerOn(R.id.restart_logging_item) {
                requireContext().sendService<LoggingService>(action = LoggingService.ACTION_RESTART_LOGGING)
            }
            setClickListenerOn(R.id.exit_item) {
                requireContext().sendService<LoggingService>(action = LoggingService.ACTION_KILL_SERVICE)
            }
        }

        logsRecycler.layoutManager = LinearLayoutManager(requireContext())
        logsRecycler.itemAnimator = null
        logsRecycler.recycledViewPool.setMaxRecycledViews(0, 50)
        logsRecycler.adapter = adapter
        logsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (changingState)
                    return

                if (viewModel.paused.value && !recyclerView.canScrollVertically(1)) {
                    if (viewModel.resumeLoggingWithBottomTouch) viewModel.resume()
                } else
                    viewModel.pause()
            }
        })

        scrollFab.setOnClickListener {
            if (viewModel.resumeLoggingWithBottomTouch)
                viewModel.resume()
            else
                scrollLogToBottom()
        }

        viewModel.queryAndFilters.collectWithLifecycle {
            val (query, filters) = it

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

        viewModel.selectedItems.collectWithLifecycle {
            val selecting = it.isNotEmpty()

            clearSelectionOnBackPressedCallback.isEnabled = selecting

            adapter.selectedItems = it
            setupToolbarForSelection(selecting, it.size)
        }

        viewModel.logs.collectWithLifecycle {
            updateLogsList(it)
        }

        viewModel.paused.collectWithLifecycle { paused ->
            changingState = true

            toolbar.menu.findItem(R.id.pause_item)
                .setIcon(if (paused) Icons.ic_play else Icons.ic_pause)
                .setTitle(if (paused) Strings.resume else Strings.pause)

            if (paused) {
                scrollFab.show()
            } else {
                scrollFab.hide()
                scrollLogToBottom()
            }

            changingState = false
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, clearSelectionOnBackPressedCallback)
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
                viewModel.selectedItems.update {
                    emptySet()
                }
            }
        } else if (viewModel.viewingFile)
            setupBackButtonForNavController()

        else invalidateNavigationButton()
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
