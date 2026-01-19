package com.f0x1d.logfox.feature.database.model

import com.f0x1d.logfox.core.recycler.Identifiable
import java.io.File

data class LogRecording(
    override val id: Long = 0,
    val title: String,
    val dateAndTime: Long,
    val file: File,
) : Identifiable {
    fun deleteFile() = file.delete()
}
