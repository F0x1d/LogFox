package com.f0x1d.logfox.feature.recordings.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.core.context.shareFileIntent
import com.f0x1d.logfox.core.tea.BaseStoreBottomSheetFragment
import com.f0x1d.logfox.core.ui.view.applyExtendedTextWatcher
import com.f0x1d.logfox.feature.recordings.presentation.databinding.SheetRecordingDetailsBinding
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsCommand
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsSideEffect
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsState
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsViewModel
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class RecordingDetailsBottomSheetFragment :
    BaseStoreBottomSheetFragment<
        SheetRecordingDetailsBinding,
        RecordingDetailsViewState,
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
        exportButton.setOnClickListener {
            send(RecordingDetailsCommand.ExportFileClicked)
        }

        shareButton.setOnClickListener {
            send(RecordingDetailsCommand.ShareRecording)
        }

        zipButton.setOnClickListener {
            send(RecordingDetailsCommand.ExportZipClicked)
        }

        textWatcher = title.applyExtendedTextWatcher {
            send(RecordingDetailsCommand.UpdateTitle(it?.toString().orEmpty()))
        }
    }

    override fun render(state: RecordingDetailsViewState) {
        textWatcher?.setText(state.currentTitle.orEmpty())

        state.recordingItem ?: return

        binding.timeText.text = state.recordingItem.formattedDate
    }

    override fun handleSideEffect(sideEffect: RecordingDetailsSideEffect) {
        when (sideEffect) {
            is RecordingDetailsSideEffect.LaunchFileExportPicker -> {
                logExportLauncher.launch(sideEffect.filename)
            }

            is RecordingDetailsSideEffect.LaunchZipExportPicker -> {
                zipLogLauncher.launch(sideEffect.filename)
            }

            is RecordingDetailsSideEffect.ShareFile -> {
                requireContext().shareFileIntent(sideEffect.file)
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
