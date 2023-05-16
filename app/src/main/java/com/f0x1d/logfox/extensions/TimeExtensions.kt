package com.f0x1d.logfox.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val logDateFormat = SimpleDateFormat("dd.MM", Locale.US)
private val logTimeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
private val exportDateFormat = SimpleDateFormat("dd-MM_HH-mm-ss", Locale.US)
private val defaultDateFormat = SimpleDateFormat("dd.MM HH:mm:ss", Locale.US)

val Long.logsDateFormatted get() = logDateFormat.format(this)
val Long.logsTimeFormatted get() = logTimeFormat.format(this)
val Long.exportFormatted get() = exportDateFormat.format(this)
val Long.defaultFormatted get() = defaultDateFormat.format(this)
fun Long.toLocaleString() = Date(this).toLocaleString()