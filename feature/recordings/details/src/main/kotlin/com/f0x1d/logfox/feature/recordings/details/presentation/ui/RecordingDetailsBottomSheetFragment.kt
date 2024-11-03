package com.f0x1d.logfox.feature.recordings.details.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.asUri
import com.f0x1d.logfox.arch.presentation.ui.dialog.BaseBottomSheetFragment
import com.f0x1d.logfox.arch.shareFileIntent
import com.f0x1d.logfox.feature.recordings.details.databinding.SheetRecordingDetailsBinding
import com.f0x1d.logfox.feature.recordings.details.presentation.RecordingDetailsViewModel
import com.f0x1d.logfox.navigation.Directions
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class RecordingDetailsBottomSheetFragment: BaseBottomSheetFragment<SheetRecordingDetailsBinding>() {

    private val viewModel by viewModels<RecordingDetailsViewModel>()

    private val zipLogLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) {
        viewModel.exportZipFile(it ?: return@registerForActivityResult)
    }
    // no plain because android will append .txt itself
    private val logExportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*")
    ) {
        viewModel.exportFile(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = SheetRecordingDetailsBinding.inflate(inflater, container, false)

    override fun SheetRecordingDetailsBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewButton.setOnClickListener {
            viewModel.currentState.recording?.let { logRecording ->
                findNavController().navigate(
                    resId = Directions.action_global_logsFragment_from_recordingBottomSheet,
                    args = bundleOf(
                        "file_uri" to logRecording.file.asUri(requireContext()),
                    ),
                )
            }
        }
        exportButton.setOnClickListener {
            viewModel.currentState.recording?.let { logRecording ->
                logExportLauncher.launch("${viewModel.formatForExport(logRecording.dateAndTime)}.log")
            }
        }
        shareButton.setOnClickListener {
            viewModel.currentState.recording?.let { logRecording ->
                requireContext().shareFileIntent(logRecording.file)
            }
        }
        zipButton.setOnClickListener {
            viewModel.currentState.recording?.let { logRecording ->
                zipLogLauncher.launch("${viewModel.formatForExport(logRecording.dateAndTime)}.zip")
            }
        }

        title.doAfterTextChanged { viewModel.updateTitle(it?.toString().orEmpty()) }

        viewModel.state.collectWithLifecycle { state ->
            title.setText(viewModel.currentTitle.orEmpty())

            val logRecording = state.recording ?: return@collectWithLifecycle

            timeText.text = Date(logRecording.dateAndTime).toLocaleString()
        }
    }
}
