package com.f0x1d.logfox.feature.database.api.entity

import java.io.File

data class LogRecordingEntity(
    val id: Long = 0,
    val title: String,
    val dateAndTime: Long,
    val file: File,
    val isCacheRecording: Boolean = false,
)
