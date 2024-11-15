package com.f0x1d.logfox.feature.recordings.list.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.presentation.ui.fragment.compose.BaseComposeFragment
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.list.presentation.RecordingsAction
import com.f0x1d.logfox.feature.recordings.list.presentation.RecordingsViewModel
import com.f0x1d.logfox.feature.recordings.list.presentation.ui.compose.RecordingsScreenContent
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecordingsFragment : BaseComposeFragment() {

    private val viewModel by viewModels<RecordingsViewModel>()

    private val listener: RecordingsScreenListener by lazy {
        RecordingsScreenListener(
            onRecordingClick = { openDetails(it) },
            onRecordingDeleteClick = {
                showAreYouSureDeleteDialog {
                    viewModel.delete(it)
                }
            },
            onStartStopClick = { viewModel.toggleStartStop() },
            onPauseResumeClick = { viewModel.togglePauseResume() },
            onClearClick = {
                showAreYouSureClearDialog {
                    viewModel.clearRecordings()
                }
            },
            onSaveAllClick = { viewModel.saveAll() },
        )
    }

    private val snackbarHostState = SnackbarHostState()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.actions.collectWithLifecycle { action ->
            when (action) {
                is RecordingsAction.ShowSnackbar -> lifecycleScope.launch {
                    snackbarHostState.showSnackbar(action.text)
                }
                is RecordingsAction.OpenRecording -> openDetails(action.recording)
            }
        }
    }

    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsState()

        RecordingsScreenContent(
            state = state,
            listener = listener,
            snackbarHostState = snackbarHostState,
        )
    }

    private fun openDetails(recording: LogRecording?) = recording?.id?.also {
        findNavController().navigate(
            resId = Directions.action_recordingsFragment_to_recordingBottomSheet,
            args = bundleOf("recording_id" to it),
        )
    }
}
