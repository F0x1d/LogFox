package com.f0x1d.logfox.feature.logging.presentation.list.model

import com.f0x1d.logfox.feature.logging.api.model.LogLine

fun LogLine.toPresentationModel(
    displayText: CharSequence,
    selected: Boolean,
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
    selected = selected,
)
