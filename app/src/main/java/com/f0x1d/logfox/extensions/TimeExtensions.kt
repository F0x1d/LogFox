package com.f0x1d.logfox.extensions

import java.text.SimpleDateFormat
import java.util.*

private val logDateFormat = SimpleDateFormat("hh:mm:ss.SSS", Locale.US)
private val exportDateFormat = SimpleDateFormat("dd-MM_hh-mm-ss", Locale.US)
private val defaultDateFormat = SimpleDateFormat("dd.MM hh:mm:ss", Locale.US)

val Long.logsFormatted get() = logDateFormat.format(this)
val Long.exportFormatted get() = exportDateFormat.format(this)
val Long.defaultFormatted get() = defaultDateFormat.format(this)