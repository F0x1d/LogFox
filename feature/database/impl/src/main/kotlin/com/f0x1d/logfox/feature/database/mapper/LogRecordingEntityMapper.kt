package com.f0x1d.logfox.feature.database.mapper

import com.f0x1d.logfox.feature.database.entity.LogRecordingEntity
import com.f0x1d.logfox.feature.database.entity.LogRecordingRoomEntity

internal fun LogRecordingRoomEntity.toData() = LogRecordingEntity(
    id = id,
    title = title,
    dateAndTime = dateAndTime,
    file = file,
    isCacheRecording = isCacheRecording,
)

internal fun LogRecordingEntity.toRoom() = LogRecordingRoomEntity(
    id = id,
    title = title,
    dateAndTime = dateAndTime,
    file = file,
    isCacheRecording = isCacheRecording,
)
