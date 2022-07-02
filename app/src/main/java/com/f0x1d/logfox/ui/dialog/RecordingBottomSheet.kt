package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.databinding.SheetRecordingBinding
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.ui.dialog.base.BaseViewModelBottomSheet
import com.f0x1d.logfox.utils.viewModelFactory
import com.f0x1d.logfox.viewmodel.recordings.RecordingViewModel
import com.f0x1d.logfox.viewmodel.recordings.RecordingViewModelAssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecordingBottomSheet: BaseViewModelBottomSheet<RecordingViewModel, SheetRecordingBinding>() {

    @Inject
    lateinit var assistedFactory: RecordingViewModelAssistedFactory

    override val viewModel by viewModels<RecordingViewModel> {
        viewModelFactory {
            assistedFactory.create(navArgs.recordingId)
        }
    }

    private val zipCrashLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) {
        viewModel.logToZip(it ?: return@registerForActivityResult) { log }
    }
    private val navArgs by navArgs<RecordingBottomSheetArgs>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetRecordingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.data.observe(viewLifecycleOwner) { logRecording ->
            if (logRecording == null) return@observe

            binding.title.text = logRecording.dateAndTime.toLocaleString()

            binding.copyLayout.setOnClickListener {
                requireContext().copyText(logRecording.log)
            }
            binding.copyLayout.setOnLongClickListener {
                findNavController().navigate(RecordingBottomSheetDirections.actionRecordingBottomSheetToRecordingExtendedCopyFragment(logRecording.log))
                return@setOnLongClickListener true
            }
            binding.shareLayout.setOnClickListener {
                requireContext().shareIntent(logRecording.log)
            }
            binding.zipLayout.setOnClickListener {
                zipCrashLauncher.launch("${logRecording.dateAndTime.exportFormatted}.zip")
            }
        }
    }
}