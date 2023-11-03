package com.f0x1d.logfox.extensions

import java.util.Date

fun Long.toLocaleString() = Date(this).toLocaleString()