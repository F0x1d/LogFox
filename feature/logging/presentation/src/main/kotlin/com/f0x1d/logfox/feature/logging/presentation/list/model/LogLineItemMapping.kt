package com.f0x1d.logfox.feature.logging.presentation.list.model

import com.f0x1d.logfox.feature.logging.api.model.LogLine

fun LogLine.toPresentationModel(
    displayText: CharSequence,
    expanded: Boolean,
    selected: Boolean,
    textSize: Float,
) = LogLineItem(
    logLineId = id,
    dateAndTime = dateAndTime,
    uid = uid,
    pid = pid,
    tid = tid,
    packageName = packageName,
    level = level,
    tag = tag,
    content = content,
    displayText = displayText,
    expanded = expanded,
    selected = selected,
    textSize = textSize,
)
