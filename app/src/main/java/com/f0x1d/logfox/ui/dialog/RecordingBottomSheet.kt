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
import java.io.File

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recording.observe(viewLifecycleOwner) { logRecording ->
            if (logRecording == null) return@observe

            binding.timeText.text = logRecording.dateAndTime.toLocaleString()

            binding.viewButton.setOnClickListener {
                findNavController().navigate(NavGraphDirections.actionGlobalLogsFragment(
                    File(logRecording.file).asUri(requireContext())
                ))
            }
            binding.exportButton.setOnClickListener {
                logExportLauncher.launch("${viewModel.dateTimeFormatter.formatForExport(logRecording.dateAndTime)}.log")
            }
            binding.shareButton.setOnClickListener {
                requireContext().shareFileIntent(File(logRecording.file))
            }
            binding.zipButton.setOnClickListener {
                zipLogLauncher.launch("${viewModel.dateTimeFormatter.formatForExport(logRecording.dateAndTime)}.zip")
            }
        }

        viewModel.currentTitle.filterNotNull().take(1).asLiveData().observe(viewLifecycleOwner) {
            binding.title.apply {
                setText(it)
                doAfterTextChanged { viewModel.updateTitle(it?.toString() ?: "") }
            }
        }
    }
}