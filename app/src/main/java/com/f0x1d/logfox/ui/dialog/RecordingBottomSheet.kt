package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.databinding.SheetRecordingBinding
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.ui.dialog.base.BaseViewModelBottomSheet
import com.f0x1d.logfox.utils.OneTimeAction
import com.f0x1d.logfox.utils.view.PauseTextWatcher
import com.f0x1d.logfox.viewmodel.recordings.RecordingViewModel
import com.f0x1d.logfox.viewmodel.recordings.RecordingViewModelAssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
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

    private val zipLogLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) {
        viewModel.logToZip(it ?: return@registerForActivityResult) { file }
    }
    private val logExportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) {
        viewModel.exportFile(it ?: return@registerForActivityResult)
    }
    private val navArgs by navArgs<RecordingBottomSheetArgs>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetRecordingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pauseTextWatcher = PauseTextWatcher {
            viewModel.updateTitle(it.toString())
        }.also {
            binding.title.addTextChangedListener(it)
        }

        val setTextAction = OneTimeAction()

        viewModel.data.observe(viewLifecycleOwner) { logRecording ->
            if (logRecording == null) return@observe

            setTextAction.doIfNotDone {
                pauseTextWatcher.paused {
                    binding.title.setText(logRecording.title)
                }
            }

            binding.timeText.text = logRecording.dateAndTime.toLocaleString()

            binding.exportLayout.setOnClickListener {
                logExportLauncher.launch("${logRecording.dateAndTime.exportFormatted}.txt")
            }
            binding.shareLayout.setOnClickListener {
                requireContext().shareFileIntent(File(logRecording.file))
            }
            binding.zipLayout.setOnClickListener {
                zipLogLauncher.launch("${logRecording.dateAndTime.exportFormatted}.zip")
            }
        }
    }
}