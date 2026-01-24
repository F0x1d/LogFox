package com.f0x1d.logfox.feature.recordings.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.context.asUri
import com.f0x1d.logfox.core.context.shareFileIntent
import com.f0x1d.logfox.core.tea.BaseStoreBottomSheetFragment
import com.f0x1d.logfox.core.ui.view.applyExtendedTextWatcher
import com.f0x1d.logfox.feature.recordings.presentation.databinding.SheetRecordingDetailsBinding
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsCommand
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsSideEffect
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsState
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsViewModel
import com.f0x1d.logfox.navigation.Directions
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
internal class RecordingDetailsBottomSheetFragment :
    BaseStoreBottomSheetFragment<
        SheetRecordingDetailsBinding,
        RecordingDetailsState,
        RecordingDetailsCommand,
        RecordingDetailsSideEffect,
        RecordingDetailsViewModel,
        >() {

    override val viewModel by viewModels<RecordingDetailsViewModel>()

    private val zipLogLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        it?.let { uri -> send(RecordingDetailsCommand.ExportZipFile(uri)) }
    }

    // no plain because android will append .txt itself
    private val logExportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> send(RecordingDetailsCommand.ExportFile(uri)) }
    }

    private var textWatcher: com.f0x1d.logfox.core.ui.view.ExtendedTextWatcher? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetRecordingDetailsBinding.inflate(inflater, container, false)

    override fun SheetRecordingDetailsBinding.onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        viewButton.setOnClickListener {
            send(RecordingDetailsCommand.ViewRecording)
        }

        exportButton.setOnClickListener {
            viewModel.state.value.recording?.let { logRecording ->
                logExportLauncher.launch(
                    "${viewModel.formatForExport(logRecording.dateAndTime)}.log",
                )
            }
        }

        shareButton.setOnClickListener {
            viewModel.state.value.recording?.let { logRecording ->
                requireContext().shareFileIntent(logRecording.file)
            }
        }

        zipButton.setOnClickListener {
            viewModel.state.value.recording?.let { logRecording ->
                zipLogLauncher.launch("${viewModel.formatForExport(logRecording.dateAndTime)}.zip")
            }
        }

        textWatcher = title.applyExtendedTextWatcher {
            send(RecordingDetailsCommand.UpdateTitle(it?.toString().orEmpty()))
        }
    }

    override fun render(state: RecordingDetailsState) {
        textWatcher?.setText(state.currentTitle.orEmpty())

        val logRecording = state.recording ?: return

        binding.timeText.text = Date(logRecording.dateAndTime).toLocaleString()
    }

    override fun handleSideEffect(sideEffect: RecordingDetailsSideEffect) {
        when (sideEffect) {
            is RecordingDetailsSideEffect.NavigateToViewRecording -> {
                findNavController().navigate(
                    resId = Directions.action_global_logsFragment_from_recordingBottomSheet,
                    args = bundleOf("file_uri" to sideEffect.file.asUri(requireContext())),
                )
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
