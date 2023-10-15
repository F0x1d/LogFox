package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.NavGraphDirections
import com.f0x1d.logfox.databinding.SheetRecordingBinding
import com.f0x1d.logfox.extensions.asUri
import com.f0x1d.logfox.extensions.exportFormatted
import com.f0x1d.logfox.extensions.logToZip
import com.f0x1d.logfox.extensions.shareFileIntent
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.extensions.views.replaceAccessibilityDelegateClassNameWithButton
import com.f0x1d.logfox.ui.dialog.base.BaseViewModelBottomSheet
import com.f0x1d.logfox.viewmodel.recordings.RecordingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import java.io.File

@AndroidEntryPoint
class RecordingBottomSheet: BaseViewModelBottomSheet<RecordingViewModel, SheetRecordingBinding>() {

    override val viewModel by viewModels<RecordingViewModel>()

    private val zipLogLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) {
        viewModel.logToZip(
            it ?: return@registerForActivityResult,
            viewModel.recording.value ?: return@registerForActivityResult
        )
    }
    // no plain because android will append .txt itself
    private val logExportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/*")) {
        viewModel.exportFile(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetRecordingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recording.observe(viewLifecycleOwner) { logRecording ->
            if (logRecording == null) return@observe

            binding.timeText.text = logRecording.dateAndTime.toLocaleString()

            binding.viewLayout.replaceAccessibilityDelegateClassNameWithButton()
            binding.viewLayout.setOnClickListener {
                findNavController().navigate(NavGraphDirections.actionGlobalLogsFragment(
                    File(logRecording.file).asUri(requireContext())
                ))
            }
            binding.exportLayout.replaceAccessibilityDelegateClassNameWithButton()
            binding.exportLayout.setOnClickListener {
                logExportLauncher.launch("${logRecording.dateAndTime.exportFormatted}.log")
            }
            binding.shareLayout.replaceAccessibilityDelegateClassNameWithButton()
            binding.shareLayout.setOnClickListener {
                requireContext().shareFileIntent(File(logRecording.file))
            }
            binding.zipLayout.replaceAccessibilityDelegateClassNameWithButton()
            binding.zipLayout.setOnClickListener {
                zipLogLauncher.launch("${logRecording.dateAndTime.exportFormatted}.zip")
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