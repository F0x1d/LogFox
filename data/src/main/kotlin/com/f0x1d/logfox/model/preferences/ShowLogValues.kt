package com.f0x1d.logfox.model.preferences

data class ShowLogValues(
    val date: Boolean,
    val time: Boolean,
    val uid: Boolean,
    val pid: Boolean,
    val tid: Boolean,
    val packageName: Boolean,
    val tag: Boolean,
    val content: Boolean
) {
    val asArray = booleanArrayOf(
        date,
        time,
        uid,
        pid,
        tid,
        packageName,
        tag,
        content
    )
}
