package com.f0x1d.logfox.feature.recordings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.context.isHorizontalOrientation
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.R
import com.f0x1d.logfox.feature.recordings.adapter.RecordingsAdapter
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingState
import com.f0x1d.logfox.feature.recordings.databinding.FragmentRecordingsBinding
import com.f0x1d.logfox.feature.recordings.viewmodel.RecordingsViewModel
import com.f0x1d.logfox.model.event.Event
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.density.dpToPx
import com.f0x1d.logfox.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setDescription
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class RecordingsFragment: BaseViewModelFragment<RecordingsViewModel, FragmentRecordingsBinding>() {

    override val viewModel by viewModels<RecordingsViewModel>()

    private val adapter = RecordingsAdapter(
        click = {
            openDetails(it)
        },
        delete = {
            showAreYouSureDeleteDialog {
                viewModel.delete(it)
            }
        },
    )

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentRecordingsBinding.inflate(inflater, container, false)

    override fun FragmentRecordingsBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireContext().isHorizontalOrientation.also { horizontalOrientation ->
            recordingsRecycler.applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = horizontalOrientation)
                }
            }

            pauseFab.applyInsetter {
                type(navigationBars = true) {
                    margin(vertical = horizontalOrientation)
                }
            }
            recordFab.applyInsetter {
                type(navigationBars = true) {
                    margin(vertical = horizontalOrientation)
                }
            }
        }

        toolbar.menu.apply {
            setClickListenerOn(R.id.logs_cache_item) {
                // TODO: return it
            }
            setClickListenerOn(R.id.save_all_logs_item) {
                viewModel.saveAll()
            }
            setClickListenerOn(R.id.clear_item) {
                showAreYouSureClearDialog {
                    viewModel.clearRecordings()
                }
            }
        }

        recordFab.setOnClickListener {
            viewModel.toggleStartStop()
        }
        pauseFab.setOnClickListener { viewModel.togglePauseResume() }

        recordingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        recordingsRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                dividerInsetStart = 80.dpToPx.toInt()
                dividerInsetEnd = 10.dpToPx.toInt()
                isLastItemDecorated = false
            }
        )
        recordingsRecycler.adapter = adapter

        viewModel.recordings.collectWithLifecycle {
            placeholderLayout.root.isVisible = it.isEmpty()

            adapter.submitList(it)
        }

        viewModel.recordingState.collectWithLifecycle { state ->
            recordFab.apply {
                when (state) {
                    RecordingState.IDLE, RecordingState.SAVING -> {
                        setImageResource(Icons.ic_recording)
                        setDescription(Strings.record)
                        isEnabled = state == RecordingState.IDLE
                    }

                    RecordingState.RECORDING, RecordingState.PAUSED -> {
                        setImageResource(Icons.ic_stop)
                        setDescription(Strings.stop)
                        isEnabled = true
                    }

                    else -> Unit
                }
            }

            pauseFab.apply {
                when (state) {
                    RecordingState.IDLE, RecordingState.SAVING -> {
                        hide()
                    }

                    RecordingState.RECORDING -> {
                        setImageResource(Icons.ic_pause)
                        setDescription(Strings.pause)
                        show()
                    }

                    RecordingState.PAUSED -> {
                        setImageResource(Icons.ic_play)
                        setDescription(Strings.resume)
                        show()
                    }
                }
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            RecordingsViewModel.EVENT_TYPE_RECORDING_SAVED -> openDetails(event.consume<LogRecording>())
        }
    }

    private fun openDetails(recording: LogRecording?) = recording?.id?.also {
        findNavController().navigate(
            resId = Directions.action_recordingsFragment_to_recordingBottomSheet,
            args = bundleOf("recording_id" to it),
        )
    }
}
