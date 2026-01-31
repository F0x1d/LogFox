package com.f0x1d.logfox.feature.recordings.presentation.model

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

fun LogRecording.toPresentationModel(formattedDate: String) = LogRecordingItem(
    recordingId = id,
    title = title,
    formattedDate = formattedDate,
)
