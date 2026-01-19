package com.f0x1d.logfox.feature.database.mapper

import com.f0x1d.logfox.feature.database.entity.LogRecordingEntity
import com.f0x1d.logfox.feature.database.model.LogRecording

internal fun LogRecordingEntity.toDomain() = LogRecording(
    id = id,
    title = title,
    dateAndTime = dateAndTime,
    file = file,
)

internal fun LogRecording.toEntity(
    isCacheRecording: Boolean = false,
) = LogRecordingEntity(
    id = id,
    title = title,
    dateAndTime = dateAndTime,
    file = file,
    isCacheRecording = isCacheRecording,
)
