package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.ViewStateMapper
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.presentation.model.toPresentationModel
import javax.inject.Inject

internal class RecordingDetailsViewStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : ViewStateMapper<RecordingDetailsState, RecordingDetailsViewState> {
    override fun map(state: RecordingDetailsState) = RecordingDetailsViewState(
        recordingItem = state.recording?.toPresentationModel(
            formattedDate = "${dateTimeFormatter.formatDate(state.recording.dateAndTime)} ${dateTimeFormatter.formatTime(state.recording.dateAndTime)}",
        ),
        currentTitle = state.currentTitle,
    )
}
