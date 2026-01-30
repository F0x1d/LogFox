package com.f0x1d.logfox.feature.logging.presentation.list.model

import com.f0x1d.logfox.core.recycler.Identifiable
import com.f0x1d.logfox.feature.logging.api.model.LogLevel

data class LogLineItem(
    val logLineId: Long,
    val dateAndTime: Long,
    val uid: String,
    val pid: String,
    val tid: String,
    val packageName: String?,
    val level: LogLevel,
    val tag: String,
    val content: String,
    val displayText: CharSequence,
    val expanded: Boolean,
    val selected: Boolean,
    val textSize: Float,
) : Identifiable {
    override val id: Any get() = logLineId
}
