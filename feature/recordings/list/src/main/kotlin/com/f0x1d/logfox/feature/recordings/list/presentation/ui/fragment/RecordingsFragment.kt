package com.f0x1d.logfox.feature.recordings.list.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.arch.isHorizontalOrientation
import com.f0x1d.logfox.arch.presentation.ui.fragment.BaseFragment
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.list.R
import com.f0x1d.logfox.feature.recordings.list.databinding.FragmentRecordingsBinding
import com.f0x1d.logfox.feature.recordings.list.presentation.RecordingsAction
import com.f0x1d.logfox.feature.recordings.list.presentation.RecordingsViewModel
import com.f0x1d.logfox.feature.recordings.list.presentation.adapter.RecordingsAdapter
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
class RecordingsFragment: BaseFragment<FragmentRecordingsBinding>() {

    private val viewModel by viewModels<RecordingsViewModel>()

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

        viewModel.state.collectWithLifecycle { state ->
            placeholderLayout.root.isVisible = state.recordings.isEmpty()

            adapter.submitList(state.recordings)

            recordFab.apply {
                when (val recordingState = state.recordingState) {
                    RecordingState.IDLE, RecordingState.SAVING -> {
                        setImageResource(Icons.ic_recording)
                        setDescription(Strings.record)
                        isEnabled = recordingState == RecordingState.IDLE
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
                when (state.recordingState) {
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

        viewModel.actions.collectWithLifecycle { action ->
            when (action) {
                is RecordingsAction.ShowSnackbar -> snackbar(action.text)
                is RecordingsAction.OpenRecording -> openDetails(action.recording)
            }
        }
    }

    private fun openDetails(recording: LogRecording?) = recording?.id?.also {
        findNavController().navigate(
            resId = Directions.action_recordingsFragment_to_recordingBottomSheet,
            args = bundleOf("recording_id" to it),
        )
    }
}
