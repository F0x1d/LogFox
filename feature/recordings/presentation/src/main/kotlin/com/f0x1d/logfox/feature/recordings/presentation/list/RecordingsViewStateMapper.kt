package com.f0x1d.logfox.feature.recordings.presentation.list

import com.f0x1d.logfox.core.tea.ViewStateMapper
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.presentation.model.toPresentationModel
import javax.inject.Inject

internal class RecordingsViewStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : ViewStateMapper<RecordingsState, RecordingsViewState> {
    override fun map(state: RecordingsState) = RecordingsViewState(
        recordings = state.recordings.map { recording ->
            recording.toPresentationModel(
                formattedDate = "${dateTimeFormatter.formatDate(recording.dateAndTime)} ${dateTimeFormatter.formatTime(recording.dateAndTime)}",
            )
        },
        recordingState = state.recordingState,
    )
}
