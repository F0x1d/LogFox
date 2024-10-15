package com.f0x1d.logfox.feature.crashes.api.controller

import com.f0x1d.logfox.model.logline.LogLine

interface CrashesController {
    val readers: List<suspend (LogLine) -> Unit>
}
