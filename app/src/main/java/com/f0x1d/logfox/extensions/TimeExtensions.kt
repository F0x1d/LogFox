package com.f0x1d.logfox.extensions

import java.text.SimpleDateFormat
import java.util.*

private val logDateFormat = SimpleDateFormat("hh:mm:ss.SSS", Locale.US)

fun Long.formatForLogs() = logDateFormat.format(this)