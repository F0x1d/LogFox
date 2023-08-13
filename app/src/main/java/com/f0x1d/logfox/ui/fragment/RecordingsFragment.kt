package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.RecordingsAdapter
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.databinding.FragmentRecordingsBinding
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.extensions.startLoggingService
import com.f0x1d.logfox.repository.logging.RecordingState
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.recordings.RecordingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordingsFragment: BaseViewModelFragment<RecordingsViewModel, FragmentRecordingsBinding>() {

    override val viewModel by viewModels<RecordingsViewModel>()

    private val adapter = RecordingsAdapter(click = {
        openDetails(it)
    }, delete = {
        viewModel.delete(it)
    })

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRecordingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.recordings_menu)
        binding.toolbar.menu.setClickListenerOn(R.id.clear_item) {
            viewModel.clearRecordings()
        }

        binding.recordFab.setOnClickListener {
            if (!viewModel.loggingServiceOrRecordingActive) {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_dialog_warning)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.recording_with_no_service_warning)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNeutralButton(R.string.start_service) { dialog, which -> requireContext().startLoggingService() }
                    .show()
            }

            viewModel.toggleStartStop()
        }
        binding.pauseFab.setOnClickListener { viewModel.togglePauseResume() }

        binding.recordingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recordingsRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                dividerInsetStart = 80.dpToPx.toInt()
                dividerInsetEnd = 10.dpToPx.toInt()
                isLastItemDecorated = false
            })
        binding.recordingsRecycler.adapter = adapter

        viewModel.recordings.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.recordingStateData.observe(viewLifecycleOwner) {
            when (it) {
                RecordingState.IDLE -> {
                    binding.recordFab.setImageResource(R.drawable.ic_recording)
                    binding.recordFab.isEnabled = true

                    binding.pauseFab.hide()
                }

                RecordingState.RECORDING -> {
                    binding.recordFab.setImageResource(R.drawable.ic_stop)
                    binding.recordFab.isEnabled = true

                    binding.pauseFab.setImageResource(R.drawable.ic_pause)
                    binding.pauseFab.show()
                }

                RecordingState.PAUSED -> {
                    binding.recordFab.setImageResource(R.drawable.ic_stop)
                    binding.recordFab.isEnabled = true

                    binding.pauseFab.setImageResource(R.drawable.ic_play)
                    binding.pauseFab.show()
                }

                RecordingState.SAVING -> {
                    binding.recordFab.setImageResource(R.drawable.ic_recording)
                    binding.recordFab.isEnabled = false

                    binding.pauseFab.hide()
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
            RecordingsFragmentDirections.actionRecordingsFragmentToRecordingBottomSheet(it)
        )
    }
}