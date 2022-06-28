package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.RecordingsAdapter
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.databinding.FragmentRecordingsBinding
import com.f0x1d.logfox.repository.RecordingState
import com.f0x1d.logfox.ui.dialog.RecordingBottomSheetDirections
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.RecyclerViewDivider
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.utils.toDrawable
import com.f0x1d.logfox.utils.toString
import com.f0x1d.logfox.viewmodel.recordings.RecordingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordingsFragment: BaseViewModelFragment<RecordingsViewModel, FragmentRecordingsBinding>() {

    override val viewModel by hiltNavGraphViewModels<RecordingsViewModel>(R.id.recordingsFragment)

    private val adapter = RecordingsAdapter {
        openDetails(it)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentRecordingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.recordings_menu)
        binding.toolbar.menu.findItem(R.id.clear_item).setOnMenuItemClickListener {
            viewModel.clearRecordings()
            return@setOnMenuItemClickListener true
        }

        binding.recordButton.setOnClickListener { viewModel.toggleStartStop() }
        binding.pauseButton.setOnClickListener { viewModel.togglePauseResume() }

        binding.recordingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recordingsRecycler.addItemDecoration(RecyclerViewDivider(requireContext(), 10.dpToPx.toInt(), 10.dpToPx.toInt()))
        binding.recordingsRecycler.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.elements = it ?: return@observe
        }

        viewModel.recordingStateData.observe(viewLifecycleOwner) {
            when (it) {
                RecordingState.IDLE -> {
                    binding.recordButton.apply {
                        icon = R.drawable.ic_recording.toDrawable(requireContext())
                        text = R.string.record.toString(requireContext())
                    }
                    binding.recordButton.isEnabled = true
                    binding.pauseButton.visibility = View.GONE
                }
                RecordingState.RECORDING -> {
                    binding.recordButton.apply {
                        icon = R.drawable.ic_stop.toDrawable(requireContext())
                        text = R.string.stop.toString(requireContext())
                    }
                    binding.recordButton.isEnabled = true
                    binding.pauseButton.apply {
                        icon = R.drawable.ic_pause.toDrawable(requireContext())
                        text = R.string.pause.toString(requireContext())
                    }
                    binding.pauseButton.visibility = View.VISIBLE
                }
                RecordingState.PAUSED -> {
                    binding.recordButton.apply {
                        icon = R.drawable.ic_stop.toDrawable(requireContext())
                        text = R.string.stop.toString(requireContext())
                    }
                    binding.recordButton.isEnabled = true
                    binding.pauseButton.apply {
                        icon = R.drawable.ic_play.toDrawable(requireContext())
                        text = R.string.resume.toString(requireContext())
                    }
                    binding.pauseButton.visibility = View.VISIBLE
                }
                RecordingState.SAVING -> {
                    binding.recordButton.apply {
                        icon = R.drawable.ic_recording.toDrawable(requireContext())
                        text = R.string.record.toString(requireContext())
                    }
                    binding.recordButton.isEnabled = false
                    binding.pauseButton.visibility = View.GONE
                }
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            RecordingsViewModel.EVENT_TYPE_RECORDING_SAVED -> openDetails(event.consume<LogRecording>())
            RecordingsViewModel.EVENT_TYPE_EXTENDED_COPY_CLICKED -> findNavController().navigate(
                RecordingBottomSheetDirections.actionRecordingBottomSheetToRecordingExtendedCopyFragment(event.consume() ?: return)
            )
        }
    }

    private fun openDetails(recording: LogRecording?) {
        recording?.id?.apply {
            findNavController().navigate(RecordingsFragmentDirections.actionRecordingsFragmentToRecordingBottomSheet(this))
        }
    }
}