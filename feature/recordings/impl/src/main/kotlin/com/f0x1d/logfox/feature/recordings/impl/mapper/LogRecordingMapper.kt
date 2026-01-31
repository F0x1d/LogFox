package com.f0x1d.logfox.feature.recordings.impl.mapper

import com.f0x1d.logfox.feature.database.api.entity.LogRecordingEntity
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

internal fun LogRecordingEntity.toDomainModel() = LogRecording(
    id = id,
    title = title,
    dateAndTime = dateAndTime,
    file = file,
)

internal fun LogRecording.toEntity(isCacheRecording: Boolean = false) = LogRecordingEntity(
    id = id,
    title = title,
    dateAndTime = dateAndTime,
    file = file,
    isCacheRecording = isCacheRecording,
)
