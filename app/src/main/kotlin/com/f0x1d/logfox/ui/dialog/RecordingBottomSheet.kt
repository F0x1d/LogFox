package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.NavGraphDirections
import com.f0x1d.logfox.databinding.SheetRecordingBinding
import com.f0x1d.logfox.extensions.asUri
import com.f0x1d.logfox.extensions.context.shareFileIntent
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.ui.dialog.base.BaseViewModelBottomSheet
import com.f0x1d.logfox.viewmodel.recordings.RecordingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take

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
        container: ViewGroup?
    ) = SheetRecordingBinding.inflate(inflater, container, false)

    override fun SheetRecordingBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.recording.observe(viewLifecycleOwner) { logRecording ->
            if (logRecording == null) return@observe

            timeText.text = logRecording.dateAndTime.toLocaleString()

            viewButton.setOnClickListener {
                findNavController().navigate(
                    NavGraphDirections.actionGlobalLogsFragment(
                        fileUri = logRecording.file.asUri(requireContext())
                    )
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

        viewModel.currentTitle.filterNotNull().take(1).asLiveData().observe(viewLifecycleOwner) {
            title.apply {
                setText(it)
                doAfterTextChanged { viewModel.updateTitle(it?.toString() ?: "") }
            }
        }
    }
}