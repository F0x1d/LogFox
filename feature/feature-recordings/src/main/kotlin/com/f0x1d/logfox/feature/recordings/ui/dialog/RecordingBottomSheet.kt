package com.f0x1d.logfox.feature.recordings.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.ui.dialog.BaseViewModelBottomSheet
import com.f0x1d.logfox.context.asUri
import com.f0x1d.logfox.context.shareFileIntent
import com.f0x1d.logfox.feature.recordings.databinding.SheetRecordingBinding
import com.f0x1d.logfox.feature.recordings.viewmodel.RecordingViewModel
import com.f0x1d.logfox.navigation.Directions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import java.util.Date

@AndroidEntryPoint
class RecordingBottomSheet: BaseViewModelBottomSheet<RecordingViewModel, SheetRecordingBinding>() {

    override val viewModel by viewModels<RecordingViewModel>()

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
    ) = SheetRecordingBinding.inflate(inflater, container, false)

    override fun SheetRecordingBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.recording.collectWithLifecycle { logRecording ->
            if (logRecording == null) return@collectWithLifecycle

            timeText.text = Date(logRecording.dateAndTime).toLocaleString()

            viewButton.setOnClickListener {
                findNavController().navigate(
                    resId = Directions.action_global_logsFragment_from_recordingBottomSheet,
                    args = bundleOf(
                        "file_uri" to logRecording.file.asUri(requireContext()),
                    ),
                )
            }
            exportButton.setOnClickListener {
                logExportLauncher.launch("${viewModel.dateTimeFormatter.formatForExport(logRecording.dateAndTime)}.log")
            }
            shareButton.setOnClickListener {
                requireContext().shareFileIntent(logRecording.file)
            }
            zipButton.setOnClickListener {
                zipLogLauncher.launch("${viewModel.dateTimeFormatter.formatForExport(logRecording.dateAndTime)}.zip")
            }
        }

        viewModel.currentTitle.filterNotNull().take(1).collectWithLifecycle {
            title.apply {
                setText(it)
                doAfterTextChanged { viewModel.updateTitle(it?.toString() ?: "") }
            }
        }
    }
}
