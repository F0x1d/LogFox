package com.f0x1d.logfox.feature.recordings.presentation.list.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.ui.compose.BaseComposeFragment
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureClearDialog
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsCommand
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsSideEffect
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsViewModel
import com.f0x1d.logfox.feature.recordings.presentation.list.ui.compose.RecordingsScreenContent
import com.f0x1d.logfox.navigation.Directions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class RecordingsFragment : BaseComposeFragment() {

    private val viewModel by viewModels<RecordingsViewModel>()

    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(viewModel) {
            viewModel.sideEffects.collect { sideEffect ->
                when (sideEffect) {
                    is RecordingsSideEffect.ShowSnackbar -> scope.launch {
                        snackbarHostState.showSnackbar(sideEffect.text)
                    }

                    is RecordingsSideEffect.OpenRecording -> openDetails(sideEffect.recordingId)

                    // Business logic side effects - handled by EffectHandler, ignored here
                    else -> Unit
                }
            }
        }

        val listener = remember {
            RecordingsScreenListener(
                onRecordingClick = { viewModel.send(RecordingsCommand.OpenRecordingDetails(it.recordingId)) },
                onRecordingDeleteClick = {
                    showAreYouSureDeleteDialog {
                        viewModel.send(RecordingsCommand.Delete(it.recordingId))
                    }
                },
                onStartStopClick = { viewModel.send(RecordingsCommand.ToggleStartStop) },
                onPauseResumeClick = { viewModel.send(RecordingsCommand.TogglePauseResume) },
                onClearClick = {
                    showAreYouSureClearDialog {
                        viewModel.send(RecordingsCommand.ClearRecordings)
                    }
                },
                onSaveAllClick = { viewModel.send(RecordingsCommand.SaveAll) },
            )
        }

        RecordingsScreenContent(
            state = state,
            listener = listener,
            snackbarHostState = snackbarHostState,
        )
    }

    private fun openDetails(recordingId: Long) {
        findNavController().navigate(
            resId = Directions.action_recordingsFragment_to_recordingBottomSheet,
            args = bundleOf("recording_id" to recordingId),
        )
    }
}
